# La Casa di Cenere - Documentazione 

**Autori:** Raffaella Nitti, Aurora Marinelli  
**Corso:** Metodi Avanzati di Programmazione  
**Università:** Università degli Studi di Bari Aldo Moro  
**Anno Accademico:** 2025/2026

---

## Descrizione dell'avventura

"La Casa di Cenere" è un'avventura testuale-grafica ambientata in una misteriosa villa avvolta da un'atmosfera gotica e surreale. Dopo essersi rifugiato al suo interno per cercare riparo da una tempesta improvvisa, il protagonista scopre ben presto di essere rimasto intrappolato tra le sue mura.

Il giocatore deve esplorare cinque stanze interconnesse, ciascuna contenente enigmi e oggetti da scoprire. L'obiettivo finale è raccogliere tre oggetti rituali (candela, amuleto e diario) e portarli all'altare della cripta per completare un antico rituale che permetterà di liberare la casa dal suo peso secolare.

### Caratteristiche del gioco

#### 5 stanze da esplorare con passaggi bloccati da risolvere:
- **Stanza 1** - Ingresso
- **Stanza 2** - Salone
- **Stanza 3** - Biblioteca
- **Stanza 4** - Salone degli specchi
- **Stanza 5** - Cripta della cenere

#### 6 comandi d'azione:
- **Osserva** - Mostra la descrizione della stanza corrente
- **Osserva [oggetto]** - Mostra la descrizione di un oggetto specifico
- **Prendi [oggetto]** - Raccogliere un oggetto
- **Usa [oggetto]** - Usare un oggetto
- **Usa [oggetto1] con [oggetto2]** - Usa due oggetti insieme
- **Lascia [oggetto]** - Lascia un oggetto nella stanza corrente

#### Funzionalità aggiuntive:
- **Sistema di inventario** per gestire gli oggetti raccolti
- **Enigmi logici** incluso un mini-gioco degli specchi nella Stanza 4
- **Interfaccia grafica** realizzata con Swing con immagini per ogni stanza
- **Database H2** per memorizzare le descrizioni dinamiche
- **Sistema di salvataggio/caricamento** tramite file JSON
- **Server REST** per visualizzare i crediti del gioco

---

## Progettazione

Il progetto è organizzato seguendo il paradigma Object-Oriented e il pattern MVC (Model-View-Controller) con una chiara separazione delle responsabilità:

### Package model
Contiene le classi che rappresentano le entità del gioco:
- **Game**: classe singleton che rappresenta lo stato globale del gioco
- **Room**: rappresenta una stanza con nome, descrizione e oggetti
- **Item** e **ItemContainer**: gerarchia per gli oggetti di gioco
- **Command**: rappresenta i comandi disponibili
- **RoomConnection**: modella i collegamenti direzionali tra stanze

### Package controller
Gestisce la logica di gioco e l'elaborazione dei comandi:
- **Parser**: analizza l'input testuale dell'utente
- **CommandHandler**: esegue i comandi usando il pattern Strategy
- **Engine**: contiene la logica specifica del gioco (uso oggetti, puzzle)
- **GameController**: coordina inizializzazione, salvataggio e caricamento

### Package view
Componenti dell'interfaccia grafica:
- **MainFrame**: frame principale con gestione CardLayout
- **MenuPanel**: menu principale
- **GamePanel**: pannello di gioco con area testo, inventario e immagini
- **HelpDialog**: finestra di aiuto
- **MapDialog**: finestra con la mappa interattiva

### Package service
Servizi di supporto:
- **InputService**: gestisce il flusso di input e gli eventi speciali
- **OutputService**: formatta e visualizza il testo con word wrapping
- **GameFlowController**: gestisce l'input asincrono dell'utente
- **MirrorGame**: implementa il mini-gioco degli specchi

### Package database
Gestione della persistenza:
- **DatabaseH2**: connessione e query al database H2

### Package server
Server REST:
- **RestServer**: avvia il server Grizzly
- **CreditsHandler**: gestisce l'endpoint /api/credits

### Package util
Classi di utilità:
- **GameSerializer**: serializzazione/deserializzazione JSON
- **GameInitializer**: inizializzazione iniziale del gioco
- **ItemDeserializer**: deserializzatore custom per oggetti
- **TextAnimator**: animazione del testo carattere per carattere
- **Music**: gestione audio

### Package type
Enumerazioni e tipi:
- **CommandType**: enum dei tipi di comando
- **CommandKey**: chiave per la mappa dei comandi
- **CommandAction**: interfaccia funzionale per i comportamenti
- **ParserOutput**: output del parser

---

## 1. Diagramma delle Classi

### Diagramma del Sistema di Parsing dei Comandi

![Diagramma delle Classi](./class-diagram.png)

Questo diagramma rappresenta il flusso di elaborazione di un comando testuale inserito dall'utente.

### Descrizione del funzionamento:

#### 1. Parser
Il **Parser** è il cuore del sistema: riceve l'input testuale dell'utente (es. "prendi candela") e lo analizza.
**Attributi principali:**
- `availableCommands`: tutti i comandi disponibili nel gioco (nord, sud, prendi, usa,...)
- `availableItems`: tutti gli oggetti presenti nel gioco
- `stopwords`: parole da ignorare (il, la, un, di,...)
- `gameManager`: riferimento al controller del gioco
**Metodo chiave:**
- `parse(input: String): ParserOutput` → analizza la stringa e restituisce un oggetto

#### 2. Command
**Command** rappresenta un singolo comando del gioco.
Il Parser confronta la prima parola dell'input con i nomi ed alias di tutti i Command per identificare cosa vuole fare l'utente.
**Attributi:**
- `name: String` - Nome del comando
- `aliases: List<String>` - Alias alternativi
- `type: CommandType` - Tipo enumerato

#### 3. CommandType
**CommandType** è l'enumerazione che definisce tutti i possibili comandi:
**Movimento:**
- `NORD`, `SUD`, `EST`, `OVEST`
**Azioni:**
- `OSSERVA`, `PRENDI`, `USA`, `LASCIA`

#### 4. Item
**Item** rappresenta un oggetto presente nel gioco.
**Attributi:**
- `name: String` - Nome dell'oggetto
- `description: String` - Descrizione
- `isPickable: boolean` - Se può essere raccolto
- `aliases: List<String>` - Nomi alternativi
Il Parser usa questi dati per riconoscere gli oggetti nominati dall'utente.

#### 5. ParserOutput
**ParserOutput** è il prodotto finale del Parser. Contiene:
- `command: CommandType` - Comando identificato (es. PRENDI)
- `item1: Item` - Primo oggetto identificato (es. Candela)
- `item2: Item` - Secondo oggetto (opzionale, per comandi come "usa X con Y")
- `args: int` - Numero di argomenti (0, 1, o 2)

#### 6. CommandHandler
**CommandHandler** riceve il ParserOutput ed esegue l'azione corrispondente.
**Funzionamento:**
1. Crea una `CommandKey` combinando `CommandType` + numero di args
2. Usa questa chiave per cercare nella `commandMap`
3. Recupera il `CommandAction` associato
4. Esegue l'azione

#### 7. CommandKey
**CommandKey** combina:
- `command: CommandType` - Tipo di comando
- `args: int` - Numero di argomenti
Serve per differenziare comandi con lo stesso tipo ma diverso numero di argomenti:
- `OSSERVA` con 0 args → osserva la stanza
- `OSSERVA` con 1 arg → osserva un oggetto specifico

#### 8. CommandAction
**CommandAction** è l'interfaccia funzionale con un solo metodo:
- `execute(parsedText: ParserOutput): void`
Ogni comando implementa questa interfaccia (tramite lambda) con la logica specifica dell'azione.

---

## Principi OOP Utilizzati

### 1. Ereditarietà
- **ItemContainer** estende **Item**, ereditando proprietà base e aggiungendo la capacità di contenere altri oggetti
- Permette di trattare contenitori come oggetti normali quando appropriato

### 2. Polimorfismo
- Gli oggetti **Item** e **ItemContainer** possono essere trattati polimorficamente nelle liste di oggetti
- Il metodo `execute()` dell'interfaccia **CommandAction** viene implementato in modi diversi per ogni comando

### 3. Incapsulamento
- Tutti gli attributi delle classi sono privati con metodi getter/setter pubblici
- La logica interna è nascosta, esponendo solo interfacce necessarie
- Esempio: **Game** nasconde la gestione dell'inventario e delle stanze

### 4. Pattern Singleton
- **Game** e **MirrorGame** usano il pattern Singleton per garantire un'unica istanza globale
- Evita duplicazioni dello stato di gioco

### 5. Pattern Strategy
- **CommandAction** è un'interfaccia funzionale che definisce il comportamento dei comandi
- **CommandHandler** usa una `HashMap<CommandKey, CommandAction>` per associare comandi a comportamenti
- Permette di aggiungere nuovi comandi senza modificare la struttura esistente

### 6. Composizione
- **Game** contiene liste di Item, Room e RoomConnection
- **Room** contiene liste di Item
- **ItemContainer** contiene una lista di Item
- Preferita all'ereditarietà per creare relazioni flessibili

### 7. Interfacce Funzionali
- **CommandAction** permette l'uso di lambda expressions per definire comportamenti
- **JsonDeserializer** è un'interfaccia funzionale per la deserializzazione custom

---

## 2. Specifica Algebrica

### Specifica Algebrica della Lista

#### Specifica Sintattica

<table>
    <thead>
        <tr>
            <th colspan="2">Tipi</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td colspan="2"><code>List</code>, <code>Item</code>, <code>Integer</code>, <code>Boolean</code></td>
        </tr>
        <tr>
            <td colspan="2" align="center"><strong>Operatori</strong></td>
        </tr>
        <tr>
            <td><code>newList() -> List</code></td>
            <td>Crea una nuova lista vuota</td>
        </tr>
        <tr>
            <td><code>add(List, Item, Integer) -> List</code></td>
            <td>Aggiunge un elemento alla lista nella posizione specificata</td>
        </tr>
        <tr>
            <td><code>isEmpty(List) -> Boolean</code></td>
            <td>Restituisce <code>true</code> se la lista è vuota altrimenti <code>false</code></td>
          </tr>
            <tr>
                <td><code>getLastIndex(List) -> Integer</code></td>
                <td>Restituisce l'ultima posizione occupata da un elemento</td>
            </tr> 
            <tr>
                <td><code>getIndex(List, Item) -> Integer</code></td>
                <td>Restituisce la posizione dell'elemento specificato</td>
            </tr> 
            <tr>
                <td><code>getItem(List, Integer) -> Item</code></td>
                <td>Restituisce l'elemento nella posizione specificata</td> 
            </tr> 
            <tr>
                <td><code>remove(List, Integer) -> List</code></td>
                <td>Rimuove dalla lista l'elemento nella posizione specificata</td>  
            </tr>
            <tr>
                <td><code>contains(List, Item) -> Boolean</code></td>
                <td>Restituisce <code>true</code> se l'elemento specificato è contenuto nella lista</td>
            </tr>
    </tbody>
</table>

> **Nota:** Item è un tipo generico, che può essere sostituito con qualsiasi altro tipo di dato. Integer e Boolean sono tipi ausiliari alla definizione della specifica algebrica della lista.

#### Osservazioni e Costruttori

<table>
  <thead>
    <tr>
      <th></th>
      <th colspan="2">Costruttori di l'</th>
    </tr>
  </thead>
  <tbody align="center">
    <tr>
      <td><strong>Osservazioni</strong></td>
      <td><code>newList</code></td>
      <td><code>add(l, it, id)</code></td>
    </tr>
    <tr>
      <td><code>isEmpty(l')</code></td>
      <td><code>true</code></td>
      <td><code>false</code></td>
    </tr>
    <tr>
      <td><code>getLastIndex(l')</code></td>
      <td><code>error</code></td>
      <td>if <code>isEmpty(l)</code> then <code>1</code> else <code>getLastIndex(l) + 1</code></td>
    </tr>
    <tr>
      <td><code>getIndex(l', it')</code></td>
      <td><code>error</code></td>
      <td>if <code>it = it'</code> then <code>id</code> else <code>getIndex(l, it')</code></td>
    </tr>
    <tr>
      <td><code>getItem(l', id')</code></td>
      <td><code>error</code></td>
      <td>if <code>id = id'</code> then <code>it</code> else <code>getItem(l, id')</code></td>
    </tr>
    <tr>
      <td><code>remove(l', id')</code></td>
      <td><code>error</code></td>
      <td>if <code>id = id'</code> then <code>l</code> else <code>add(remove(l, id'), it)</code></td>
    </tr>
    <tr>
      <td><code>contains(l', it')</code></td>
      <td><code>false</code></td>
      <td>if <code>it = it'</code> then <code>true</code> else <code>contains(l, it')</code></td>
    </tr>
  </tbody>
</table>

#### Specifica Semantica
- **DECLARE**
  - <code>l</code>, <code>l'</code>: <code>List</code>
  - <code>it</code>, <code>it'</code>: <code>Item</code>
  - <code>id</code>, <code>id'</code>: <code>Integer</code>

- **OPERATIONS**
  - <code>isEmpty(newList)</code> = <code>true</code>
  - <code>isEmpty(add(l, it, id))</code> = <code>false</code>
  - <code>getLastIndex(add(l, it, id))</code> = if <code>isEmpty(l)</code> then <code>1</code> else <code>getLastIndex(l) + 1</code>
  - <code>getIndex(add(l, it, id), it')</code> = if <code>it = it'</code> then <code>id</code> else <code>getIndex(l, it')</code>
  - <code>getItem(add(l, it, id), id')</code> = if <code>id = id'</code> then <code>it</code> else <code>getItem(l, id')</code>
  - <code>remove(add(l, it, id), id')</code> = if <code>id = id'</code> then <code>l</code> else <code>add(remove(l, id'), it)</code>
  - <code>contains(newList, it')</code> = <code>false</code>
  - <code>contains(add(l, it, id), it')</code> = if <code>it = it'</code> then <code>true</code> else <code>contains(l, it')</code>

#### Specifica di Restrizione
- **RESTRICTIONS**
  - <code>getLastIndex(newList)</code> = <code>error</code>
  - <code>getIndex(newList, it')</code> = <code>error</code>
  - <code>getItem(newList, id')</code> = <code>error</code>
  - <code>remove(newList, id')</code> = <code>error</code>

---

### Specifica Algebrica della Mappa

#### Specifica Sintattica
<table>
    <thead>
        <tr>
            <th colspan="2">Tipi</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td colspan="2">Map, Key, Value, Boolean, Integer</td>
        </tr>
        <tr>
            <td colspan="2"><strong>Operatori</strong></td>
        </tr>
        <tr>
            <td><code>newMap() -> Map</code></td>
            <td>Crea una nuova mappa vuota</td>
        </tr>
        <tr>
            <td><code>isEmpty(Map) -> Boolean</code></td>
            <td>Restituisce <code>true</code> se la mappa è vuota, <code>false</code> altrimenti</td>
        </tr>
        <tr>
            <td><code>put(Map, Key, Value) -> Map</code></td>
            <td>Aggiunge una coppia chiave-valore alla mappa, o, se già presente, ne aggiorna il valore</td>
        </tr>
        <tr>
            <td><code>get(Map, Key) -> Value</code></td>
            <td>Restituisce il valore associato alla chiave specificata</td>
        </tr>
        <tr>
            <td><code>containsKey(Map, Key) -> Boolean</code></td>
            <td>Restituisce <code>true</code> se la chiave specificata è presente nella mappa</td>
        </tr> 
        <tr>
            <td><code>containsValue(Map, Value) -> Boolean</code></td>
            <td>Restituisce <code>true</code> se il valore specificato è presente nella mappa</td> 
        </tr>
        <tr>
            <td><code>remove(Map, Key) -> Map</code></td>
            <td>Rimuove la chiave ed il valore associato ad essa dalla mappa</td>
        </tr> 
        <tr>
            <td><code>size(map) -> Integer</code></td>
            <td>Restituisce il numero di coppie chiave-valore presenti nella mappa</td>  
        </tr>
    </tbody>
</table>

#### Osservazioni e Costruttori

**Osservazioni:**

<table>
  <thead>
    <tr>
      <th></th>
      <th colspan="2">Costruttori di m'</th>
    </tr>
  </thead>
  <tbody align="center">
    <tr>
      <td><strong>Osservazioni</strong></td>
      <td><code>newMap</code></td>
      <td><code>put(m, k, v)</code></td>
    </tr>
    <tr>
      <td><code>isEmpty(m')</code></td>
      <td><code>true</code></td>
      <td><code>false</code></td>
    </tr>
    <tr>
      <td><code>containsKey(m', k')</code></td>
      <td><code>false</code></td>
      <td>if <code>k = k'</code> then <code>true</code> else <code>containsKey(m, k')</code></td>
    </tr>
    <tr>
      <td><code>containsValue(m', v')</code></td>
      <td><code>false</code></td>
      <td>if <code>v = v'</code> then <code>true</code> else <code>containsValue(m, v')</code></td>
    </tr>
    <tr>
      <td><code>get(m', k')</code></td>
      <td><code>error</code></td>
      <td>if <code>k = k'</code> then <code>v</code> else <code>get(m, k')</code></td>
    </tr>
    <tr>
      <td><code>remove(m', k')</code></td>
      <td><code>error</code></td>
      <td>if <code>k = k'</code> then <code>m</code> else <code>put(remove(m, k'), k, v)</code></td>
    </tr>
    <tr>
      <td><code>size(m')</code></td>
      <td><code>0</code></td>
      <td>if <code>isEmpty(m)</code> then <code>1</code> else <code>size(m) + 1</code></td>
    </tr>
  </tbody>
</table>

#### Specifica Semantica

- **DECLARE**
  - <code>m</code>, <code>m'</code>: <code>Map</code>
  - <code>k</code>, <code>k'</code>: <code>Key</code>
  - <code>v</code>, <code>v'</code>: <code>Value</code>

- **OPERATIONS**
  - <code>isEmpty(newMap)</code> = <code>true</code>
  - <code>isEmpty(put(m, k, v))</code> = <code>false</code>
  - <code>containsKey(newMap, k')</code> = <code>false</code>
  - <code>containsKey(put(m, k, v), k')</code> = if <code>k = k'</code> then <code>true</code> else <code>containsKey(m, k')</code>
  - <code>containsValue(newMap, v')</code> = <code>false</code>
  - <code>containsValue(put(m, k, v), v')</code> = if <code>v = v'</code> then <code>true</code> else <code>containsValue(m, v')</code>
  - <code>get(put(m, k, v), k')</code> = if <code>k = k'</code> then <code>v</code> else <code>get(m, k')</code>
  - <code>remove(put(m, k, v), k')</code> = if <code>k = k'</code> then <code>m</code> else <code>put(remove(m, k'), k, v)</code>
  - <code>size(newMap)</code> = <code>0</code>
  - <code>size(put(m, k, v))</code> = <code>size(m) + 1</code>

#### Specifica di Restrizione

- **RESTRICTIONS**
  - <code>get(newMap, k')</code> = <code>error</code>
  - <code>remove(newMap, k')</code> = <code>error</code>

> Queste specifiche algebriche descrivono formalmente il comportamento delle strutture dati Lista e Mappa utilizzate nell'implementazione del nostro gioco "La Casa di Cenere". Le operazioni definite corrispondono alle funzionalità implementate tramite le classi ArrayList e HashMap di Java.

---

## 3. Dettagli Implementativi

### 1. Programmazione Generica

Il progetto fa uso estensivo dei **generics** forniti dal Java Collections Framework per garantire type safety e riusabilità del codice. L'utilizzo dei generics elimina la necessità di cast espliciti e fornisce controlli di tipo a compile-time, prevenendo errori a runtime.

#### Principali strutture dati generiche utilizzate:

- `List<Item>` - per gestire inventario, oggetti nelle stanze e contenuti dei container
- `Set<Item>` - per garantire unicità degli oggetti disponibili
- `Map<String, Item>` - per assegnare nomi univoci agli oggetti e permettere ricerche efficienti
- `HashMap<CommandKey, CommandAction>` - per mappare comandi alle loro implementazioni usando il pattern Strategy
- `Set<Command>` - per gestire l'insieme dei comandi disponibili nel gioco
- `List<RoomConnection>` - per rappresentare la mappa dei collegamenti tra le stanze
- `List<String>` - per gli alias di oggetti e comandi


È stata implementata la classe **ItemContainer** che estende **Item** e permette di creare oggetti contenitori (come lo Scrigno nel gioco), ma non è una classe generica personalizzata:

```java
public class ItemContainer extends Item {

    private List<Item> list = new ArrayList<>();
    
    public void add(Item item) {
        list.add(item);
    }
    
    public void remove(Item item) {
        list.remove(item);
    }
}
```

Questa implementazione:
- Realizza il **pattern Composite** permettendo agli Item di contenere altri Item
- Utilizza `List<Item>` (generics del framework) per la gestione interna
- Permette di gestire oggetti come scrigni, borse, contenitori in modo uniforme

---

### 2. File

Il progetto gestisce il salvataggio e il caricamento dello stato di gioco attraverso la **serializzazione JSON**, offrendo maggiore flessibilità e leggibilità rispetto alla serializzazione Java standard.

Questa funzionalità è implementata attraverso la classe **GameSerializer**, che si basa sulla libreria **Gson** di Google per la conversione tra oggetti Java e formato JSON.

#### Funzionamento

La classe converte lo stato del gioco (Game e relativi Item) nei file:
- `LoadedGame.json`
- `LoadedItems.json`

E li ricostruisce al momento del caricamento.

#### Metodi principali:

**1. processJsonFiles**
```java
private Map<String, Item> processJsonFiles(String gameFilePath, String itemsFilePath)
```
Legge i file JSON passati come parametri e li restituisce in formato Java, creando un oggetto di tipo Map contenente tutti gli oggetti presenti nel gioco ed inizializzando il gioco.

**2. convertGameToJson**
```java
public void convertGameToJson()
```
Converte l'oggetto Game in formato JSON.

**3. convertItemsToJson**
```java
public void convertItemsToJson()
```
Converte gli oggetti di tipo Item in formato JSON, salvando solo gli oggetti che non sono presenti nell'inventario o in una stanza.

#### ItemDeserializer Custom

Dato che il nostro gioco contiene una gerarchia complessa di oggetti (come Item e ItemContainer), abbiamo registrato un **ItemDeserializer** personalizzato, così da garantire la corretta ricostruzione delle classi derivate durante la deserializzazione. 

Questo livello di controllo non sarebbe stato ottenibile con la semplice interfaccia `java.io.Serializable`.

---

### 3. Database (JDBC)

Il gioco utilizza un database relazionale **H2** per la gestione della sua struttura statica. La scelta di H2 è strategica in quanto si tratta di un database embedded, il che significa che:

- Il database è un semplice file locale che viene distribuito insieme all'applicazione
- Non richiede l'installazione di server esterni o configurazioni complesse

#### Classe DatabaseH2

La gestione della connessione e delle operazioni è centralizzata nella classe **DatabaseH2**, la quale:

- Definisce le costanti per il driver JDBC, l'URL del database, l'utente e la password per la connessione
- Implementa metodi per la connessione al database, la creazione delle tabelle, l'inserimento e la lettura dei dati

#### Metodi principali:

**1. connect()**
```java
public static Connection connect()
```
Stabilisce la connessione al database H2 e si occupa dell'inizializzazione delle tabelle e del popolamento iniziale dei dati se il database è vuoto.

**2. close()**
```java
public static void close(Connection conn)
```
Si occupa di chiudere la connessione al database.

**3. printFromDB()**
```java
public static void printFromDB(String comando, String stanza, String stato, 
                               String oggetto1, String oggetto2)
```
Stampa la descrizione di un elemento (comando, stanza, stato, oggetto1, oggetto2) ed utilizza una **PreparedStatement** per eseguire una query altamente specifica sulla tabella DESCRIZIONE in base a cinque parametri forniti.

```java
String query = "SELECT DESCRIZIONE FROM DESCRIZIONE WHERE " +
               "COMANDO = ? AND STANZA = ? AND STATO = ? AND " +
               "OGGETTO1 = ? AND OGGETTO2 = ?";
```

---

### 4. Lambda Expressions

Le **lambda expressions** sono state utilizzate in vari punti del progetto per rendere il codice più conciso e leggibile. Di seguito i casi più significativi:

#### 1. CommandHandler

Nella classe **CommandHandler**, le lambda expressions sono state utilizzate per definire i comportamenti dei comandi attraverso l'interfaccia funzionale **CommandAction**. Questo approccio ha permesso di creare una mappa di comandi con comportamenti associati in modo dichiarativo.

```java
commandMap.put(new CommandKey(CommandType.PRENDI, 1),
    p -> {
        if (game.getInventory().contains(p.getItem1())) {
            OutputService.displayText("Hai già " + p.getItem1().getName());
            return;
        }
        
        if (game.getCurrentRoom().getItems().contains(p.getItem1())) {
            if (p.getItem1().isPickable()) {
                game.addInventory(p.getItem1());
                game.getCurrentRoom().removeItem(p.getItem1().getName());
                OutputService.displayText("Hai raccolto " + p.getItem1().getName());
            } else {
                OutputService.displayText(p.getItem1().getName() + " non può essere raccolto.");
            }
        }
    });
```

L'interfaccia funzionale **CommandAction** definisce un singolo metodo astratto che accetta un **ParserOutput**, permettendo l'uso di lambda expressions per implementazioni concise dei vari comandi di gioco.

#### 2. Parser

Nel **Parser**, per la ricerca di comandi ed oggetti:

```java
for (Command command : availableCommands) {
    if (command.getName().equalsIgnoreCase(words[0]) ||
        command.getAliases().stream()
               .anyMatch(alias -> alias.equalsIgnoreCase(words[0]))) {
        output.setCommand(command.getType());
        output.setArgs(0);
        break;
    }
}
```

#### 3. GameController

Nella classe **GameController**, per l'aggregazione di oggetti da tutte le istanze:

```java
Set<Room> rooms = game.getCorridorMap().stream()
        .flatMap(c -> Stream.of(c.getStartingRoom(), c.getArrivingRoom()))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
```

#### 4. Game

Nella classe **Game**, per la conversione dell'inventario in array di stringhe:

```java
List<String> itemsNames = game.getInventory().stream()
        .map(Item::getName)
        .toList();
String[] itemsNamesArray = itemsNames.toArray(new String[0]);
```

#### Utilizzo in Swing e Thread

Le lambda expressions sono state utilizzate anche per semplificare notevolmente la gestione degli eventi in Swing e per creare e gestire thread in modo più leggibile:

```java
newGameButton.addActionListener(evt -> {
    gameManager.createGame();
    Game game = Game.getInstance();
    new Thread(() -> InputService.setUpGameFlow(game)).start();
});
```

---

### 5. SWING

Il progetto è stato sviluppato interamente come applicazione desktop, e per la realizzazione dell'interfaccia grafica (GUI) abbiamo utilizzato il framework **Java Swing**.

#### 1. MainFrame - Frame principale

La classe **MainFrame**, che estende `JFrame`, rappresenta il contenitore principale dell'applicazione che utilizza un **CardLayout** per gestire la transizione tra il menu principale ed il pannello di gioco.

```java
public class MainFrame extends JFrame {
    public static GamePanel game;
    
    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("La Casa di Cenere");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        
        JPanel cards = new JPanel(new CardLayout());
        MenuPanel menu = new MenuPanel();
        game = new GamePanel();
        
        cards.add(menu, "MenuPanel");
        cards.add(game, "GamePanel");
        
        add(cards);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
```

Questo layout agisce come una pila di schede, permettendo di mostrare solo un pannello alla volta.


#### 2. MenuPanel - Menu principale

La classe **MenuPanel**, che estende `JPanel`, presenta il menu iniziale con sfondo personalizzato e pulsanti stilizzati.

```java
public class MenuPanel extends JPanel {
    private JPanel backgroundPanel;
    private JButton newGame;
    private JButton help;
    private JButton loadGame;
    private JButton credits;
    
    GameController gameManager = new GameController();
    
    public MenuPanel() {
        initComponents();

        Music.getInstance().startMusic();
    }
    
}
```

L'interattività è gestita attraverso gli **ActionListener** assegnati ai `JButton`. I pulsanti utilizzano `MetalButtonUI` personalizzata per ottenere l'effetto visivo desiderato.

```java
newGame.setUI(new MetalButtonUI() {
               protected Color getSelectColor() {
                   return COLD_SELECT_COLOR;
               }
        });
        newGame.setFocusPainted(false);
        newGame.setBackground(SEMI_TRANSPARENT_BG);
        newGame.setForeground(COLD_LIGHT);
        newGame.setFont(new Font("Otacon", Font.BOLD, 24));
        newGame.setBorderPainted(true);
        newGame.setBorder(BorderFactory.createLineBorder(COLD_LIGHT, 3));
        newGame.setText("NUOVA PARTITA");
        newGame.setOpaque(true);
        newGame.setContentAreaFilled(true);
        newGame.setMaximumSize(new Dimension(240, 60));
        newGame.setMinimumSize(new Dimension(240, 60));
        newGame.setPreferredSize(new Dimension(240, 60));
        newGame.addActionListener(this::newGameActionPerformed);
```

#### 3. GamePanel - Pannello di gioco principale

La classe **GamePanel**, che estende `JPanel`, rappresenta il cuore dell'interfaccia di gioco e comprende:

- **Toolbar** con i seguenti pulsanti: indietro, mappa, salva, aiuto

```java
public class GamePanel extends JPanel {

    private JButton goBackButton;
    private JButton saveGameButton;
    private JButton helpButton;
    private JButton toggleMapButton;

    private static JPanel imagePanel;
    private static JTextPane displayTextPane;
    private JScrollPane scrollPaneDisplayText;

    private static JTextArea inventoryTextArea;
    private JScrollPane scrollPaneInventoryText;

    private JTextField userInputField;
    private JToolBar toolBar;

    private static CardLayout cardLayout;

    public GamePanel() {
        UIManager.put("ScrollBar.width", 12);
        SwingUtilities.updateComponentTreeUI(this);
        initComponents();
        initImagePanel();
    }
```

- **Pannello immagini con CardLayout**: visualizza le immagini delle 5 stanze

```java
for(int i = 1; i <= 6; i++) {
            final int roomNumber = i;
            final String imagePath = "src/main/resources/img/Stanza" + roomNumber + ".png";
            imagePanel.add(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ImageIcon image = new ImageIcon(imagePath);
                    g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }, "Stanza" + roomNumber);
        }
```

- **JTextPane con auto-scroll**: per visualizzare la narrazione e l'output dei comandi

```java
displayTextPane.setEditable(false);
        displayTextPane.setFocusable(false);
        displayTextPane.setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayTextPane.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        displayTextPane.setOpaque(true);
        displayTextPane.setBackground(DARK_FOG_CONTENT);
        displayTextPane.setForeground(COLD_LIGHT);
        
        DefaultCaret caret = (DefaultCaret) displayTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPaneDisplayText.setBackground(DARK_FOG_CONTENT);
        scrollPaneDisplayText.setViewportView(displayTextPane);
        scrollPaneDisplayText.setPreferredSize(new Dimension(550, 120));
        scrollPaneDisplayText.setMinimumSize(new Dimension(550, 120));
        scrollPaneDisplayText.setMaximumSize(new Dimension(550, 120));
        scrollPaneDisplayText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneDisplayText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneDisplayText.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 0, COLD_LIGHT));
```

- **JTextArea per l'inventario**: mostra gli oggetti posseduti dal giocatore in tempo reale

```java
inventoryTextArea.setEditable(false);
        inventoryTextArea.setOpaque(true);
        inventoryTextArea.setBackground(DARK_FOG_CONTENT);
        inventoryTextArea.setForeground(COLD_LIGHT);
        inventoryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inventoryTextArea.setText(" Inventario:\n");
        inventoryTextArea.setLineWrap(true);
        inventoryTextArea.setWrapStyleWord(true);

        scrollPaneInventoryText.setViewportView(inventoryTextArea);
        scrollPaneInventoryText.setPreferredSize(new Dimension(200, 550));
        scrollPaneInventoryText.setBorder(BorderFactory.createMatteBorder(0, 5, 5, 5, COLD_LIGHT));
```
- **JTextField per l'input utente**: permette di inserire i comandi

```java
userInputField.setMargin(new Insets(0, 8, 0, 8));
        userInputField.setForeground(COLD_LIGHT);
        userInputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        userInputField.setOpaque(true);
        userInputField.setBackground(DARK_FOG_CONTENT);
        userInputField.setCaretColor(COLD_LIGHT);
        userInputField.setBorder(BorderFactory.createMatteBorder(0, 5, 5, 0, COLD_LIGHT));
        userInputField.addActionListener(this::userInputFieldActionPerformed);
        
        GameFlowController.startInputListener();
```

#### 4. HelpDialog - Finestra di aiuto

La classe **HelpDialog** rappresenta una finestra di dialogo separata, che viene mostrata al click del pulsante "Help", garantendo che l'utente possa consultare le regole senza perdere di vista l'interfaccia principale.

```java
public class HelpDialog extends JFrame {
    
    private static HelpDialog instance;
    
    HelpDialog() {
        initComponents();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
    
    public static HelpDialog getInstance() {
        if(instance == null) {
            instance = new HelpDialog();
        }
        return instance;
    }
```

#### 5. MapDialog - Mappa interattiva

La classe **MapDialog** rappresenta una finestra separata, che visualizza una mappa grafica delle stanze con:

- Disegno custom delle stanze e corridoi
- Indicazione della stanza corrente
- Stanze visitate evidenziate
- Interattività con hover e click

```java
public class MapDialog extends JFrame {

    private static MapDialog instance;

    private MapPanel mapPanel;

    private MapDialog() {
        setTitle("Mappa - La Casa di Cenere");
        setPreferredSize(new Dimension(700, 650));
        setMinimumSize(new Dimension(700, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 30, 35));

        mapPanel = new MapPanel(Game.getInstance());
        add(mapPanel);
        pack();
    }

    public static MapDialog getInstance() {
        if (instance == null) {
            instance = new MapDialog();
        }
        return instance;
    }
```

##### Caratteristiche chiave dell'interfaccia:

- **Palette colori coerente**: uso di colori freddi (azzurro/grigio) per creare un'atmosfera gotica
- **Layout responsivo**: utilizzo di `GroupLayout` per posizionamento preciso dei componenti
- **Componenti personalizzati**: override di `paintComponent()` per rendering custom
- **Gestione degli eventi**: listener per mouse, tastiera e azioni dei pulsanti
- **Animazioni**: transizioni fluide tra pannelli e effetti di testo

L'interfaccia Swing offre un'esperienza utente completa e immersiva, integrando perfettamente la logica di gioco con una presentazione visiva curata.

---

### 6. Thread e Programmazione Concorrente

Abbiamo utilizzato i **Thread** per la gestione dell'input da parte dell'utente e l'animazione del testo.

#### 1. GameFlowController - Thread di ascolto dell'input

**GameFlowController** è una classe che impiega esplicitamente un thread dedicato per monitorare in modo continuo l'input dell'utente senza bloccare l'interfaccia grafica o il resto del flusso di gioco.

Il thread viene avviato nel metodo `startInputListener()`, dove viene creata un'istanza anonima di `Thread` contenente un ciclo infinito che controlla periodicamente la presenza di nuovo input utente.
```java
public static void startInputListener() {
    new Thread(() -> {
        while (true) {
            if (!isUserInputEmpty()) {
                InputService.gameFlow(input);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }).start();
}
```

Per evitare condizioni di gara nella lettura e scrittura dell'input, i metodi `getUserInput()`, `setUserInput()` e `isUserInputEmpty()` sono dichiarati **synchronized**. Questo garantisce che il thread di ascolto e eventuali altri componenti che modificano l'input non accedano contemporaneamente alla variabile condivisa, preservando la coerenza del buffer di input.

Il thread dedicato esegue il seguente comportamento:

- Verifica ciclicamente, ogni 100 ms, se è stato inserito nuovo input
- Quando l'input è disponibile, lo recupera tramite `getUserInput()` e lo passa a `InputService.gameFlow()`, avviando l'elaborazione della logica di gioco
- Continua l'ascolto in modo indefinito, garantendo una gestione reattiva dell'interazione utente

Questo approccio consente di mantenere la GUI completamente reattiva mentre il gioco controlla costantemente l'input senza bloccare il thread principale.

#### 2. TextAnimator - Thread per animazione del testo

**TextAnimator** è una classe che estende `Thread` e serve per creare piccoli effetti di animazione del testo sulla GUI:

- **SCRITTURA**: scrive testo lentamente, carattere per carattere
- **PAUSA**: esegue una pausa (sleep) senza bloccare l'interfaccia principale

Estendendo la classe `Thread`, ogni istanza di `TextAnimator` rappresenta un'animazione indipendente, che può essere avviata con il metodo `start()` ed eseguita in parallelo al resto dell'applicazione.

```java
public class TextAnimator extends Thread {

    private static boolean isWriting = false;
```

Per controllare l'esecuzione degli effetti di scrittura, la classe utilizza un flag statico (`isWriting`) che indica se è attualmente attivo un thread di animazione del testo. Tale variabile è gestita all'interno di blocchi sincronizzati per garantire l'accesso concorrente sicuro ed evitare la sovrapposizione di più effetti di scrittura simultanei.

Il metodo `run()` seleziona l'effetto da eseguire in base al tipo specificato:

- **SCRITTURA**: il testo viene mostrato carattere per carattere tramite il metodo `effettoScritturaGUI()`, con una pausa programmata tra un carattere e il successivo. L'uso di `Thread.sleep()` permette di simulare l'effetto "macchina da scrivere" senza bloccare l'interfaccia.
- **PAUSA**: il thread esegue semplicemente una sospensione temporale (`Thread.sleep(durata)`), utile per introdurre ritardi controllati nelle sequenze di testo.

Al termine dell'animazione, la variabile `isWriting` viene riportata a `false` nel blocco `finally`, assicurando che lo stato dell'animatore sia sempre coerente anche in caso di eccezioni o interruzioni del thread.

---

### 7. Socket e/o REST

Nel nostro progetto abbiamo implementato un server REST locale utilizzando **Grizzly HTTP Server** per esporre un endpoint che visualizza i crediti del gioco.

#### 1. RestServer - Avvio del server

La classe **RestServer** gestisce l'avvio e la configurazione del server Grizzly sulla porta 8080.

```java
public class RestServer {
    private HttpServer server;
    
    public void startServer() throws IOException {
        server = HttpServer.createSimpleServer("/", 8080);
        ServerConfiguration config = server.getServerConfiguration();
        
        config.addHttpHandler(new CreditsHandler(), "/api/credits");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdownNow();
        }));

        new Thread(() -> {
            try {
                server.start();
                Thread.currentThread().join();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void stopServer() {
        if (server != null) {
            server.shutdownNow();
        }
    }
}
```

Il server viene configurato per:

- Ascoltare sulla porta 8080 (standard per applicazioni HTTP alternative)
- Registrare un handler per gestire le richieste all'endpoint `/api/credits`
- Avviarsi in un thread separato per non bloccare l'applicazione principale
- Chiudersi automaticamente quando l'applicazione termina

Il server viene avviato automaticamente all'avvio dell'applicazione nel `main()`:

```java
try {
    RestServer server = new RestServer();
    server.startServer();
} catch (Exception e) {
    System.err.println("Errore nell'avvio del server REST: " + e.getMessage());
    e.printStackTrace();
}
```

#### 2. CreditsHandler - Gestione dell'endpoint

La classe **CreditsHandler** estende `HttpHandler` e gestisce le richieste HTTP all'endpoint `/api/credits`:

```java
public class CreditsHandler extends HttpHandler {
    
    @Override
    public void service(Request request, Response response) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
```

Il metodo `service()` implementa la logica REST completa:

1. **Configurazione CORS**: permette richieste da qualsiasi origine (utile per sviluppo e test)
2. **Gestione preflight**: risponde alle richieste OPTIONS usate dai browser moderni
3. **Validazione metodo HTTP**: accetta solo GET, rifiutando POST/PUT/DELETE (seguendo il principio REST che GET è idempotente e safe)
4. **Gestione errori**: restituisce codici HTTP appropriati (405 per metodo non permesso, 500 per errori interni)

Il metodo `sendHtmlResponse()` prepara ed invia la risposta HTTP, che include:

- **Status code 200**: indica che la richiesta è stata elaborata con successo
- **Content-Type**: specifica che il contenuto è HTML con encoding UTF-8
- **Body**: contiene l'HTML generato dinamicamente dal metodo `generateHtml()`

```java
private void sendHtmlResponse(Response response) throws IOException {
        response.setStatus(200);
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String html = generateHtml();
        
        OutputStream out = response.getOutputStream();
        out.write(html.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
```

Il metodo `generateHtml()` costruisce la pagina HTML che include:

- **Stili CSS inline**: permettono di creare un design coerente con l'atmosfera gotica del gioco senza file esterni
- **Struttura semantica**: utilizza tag HTML appropriati (h1, h2, ul, li) per una corretta organizzazione del contenuto
- **Design responsive**: layout centrato con larghezza massima per ottimale visualizzazione su schermi di diverse dimensioni


Il server implementa una gestione completa degli errori HTTP con il metodo `sendErrorResponse()`:

```java
private void sendErrorResponse(Response response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        StringBuilder errorHtml = new StringBuilder();
        
        OutputStream out = response.getOutputStream();
        out.write(errorHtml.toString().getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
```

Nel menu principale del gioco, **MenuPanel**, è presente il bottone "RICONOSCIMENTI", che effettua una richiesta al server REST locale:

```java
private void creditsActionPerformed(ActionEvent evt) {
        try {
            String apiUrl = "http://localhost:8080/api/credits";
        
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI(apiUrl));
                } else {
                    showMessageDialog(this, 
                        "Il sistema non supporta l'apertura del browser.\n" +
                        "Apri manualmente: " + apiUrl, 
                        "Info", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                showMessageDialog(this, 
                    "Desktop non supportato su questo sistema.\n" +
                    "Apri manualmente: " + apiUrl, 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
         }
        private void creditsActionPerformed(ActionEvent evt) {
        try {
            String apiUrl = "http://localhost:8080/api/credits";
        
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI(apiUrl));
                } else {
                    showMessageDialog(this, 
                        "Il sistema non supporta l'apertura del browser.\n" +
                        "Apri manualmente: " + apiUrl, 
                        "Info", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                showMessageDialog(this, 
                    "Desktop non supportato su questo sistema.\n" +
                    "Apri manualmente: " + apiUrl, 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
         }
```

Quando l'utente clicca sul bottone:

1. Viene costruito l'URL dell'endpoint REST: `http://localhost:8080/api/credits`
2. L'API `Desktop.browse()` apre il browser predefinito
3. Il browser effettua automaticamente una richiesta GET HTTP all'URL specificato
4. Il server REST riceve la richiesta e restituisce la pagina HTML
5. Il browser visualizza il contenuto ricevuto

---

## Conclusioni

"La Casa di Cenere" è stata per noi un'occasione per unire creatività e programmazione, trasformando un'idea in un'avventura completa. Partendo da una semplice villa misteriosa, abbiamo costruito un gioco che vuole coinvolgere il giocatore con enigmi, oggetti da trovare e un'atmosfera un po' gotica e surreale.

Durante lo sviluppo abbiamo imparato a organizzare meglio il lavoro, a collaborare e a capire quanto ogni dettaglio – dalla grafica, ai comandi, fino alle descrizioni – possa cambiare l'esperienza finale. Realizzare l'interfaccia, gestire le stanze, scrivere le parti narrative e far funzionare tutto insieme è stato impegnativo, ma anche molto gratificante.

Questo progetto per noi rappresenta non solo un gioco, ma un percorso: fatto di problemi risolti, idee cambiate in corsa e tante soddisfazioni. "La Casa di Cenere" è il risultato di ciò che abbiamo imparato e della voglia di creare qualcosa di nostro, e speriamo che chi lo gioca possa ritrovare almeno un po' del divertimento che noi abbiamo messo nel realizzarlo.

---
