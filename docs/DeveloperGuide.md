---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TutorConnect Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 (AB3) project created by the [SE-EDU initiative](https://se-education.org).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2425S2-CS2103-F08-4/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2425S2-CS2103-F08-4/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**Note:** When describing classes by `XYClass`, `Y` refers broadly to contact/event/todo and `X` refers to the action the command should perform. 

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `contact delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `YManager` class which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside components from being coupled to the implementation of a component), as illustrated in the class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="600" />

As shown in the diagram, TutorConnect's component managers are organized as follows:

* **Logic Component**: Defines the `LogicManager` which serves as the central coordinator for executing commands.
* **Model Component**: Implements the `ModelManager` which manages three specialized data managers:
    * `ContactManagerAndList`: Manages the contacts (students, tutors) in the system
    * `TodoManagerAndList`: Handles todo items and tasks
    * `EventManagerAndList`: Manages scheduled events and appointments
* **Storage Component**: Uses the `StorageManager` to coordinate storage operations across specialized storage handlers:
    * `ContactStorage`: Persists contact data
    * `TodoStorage`: Stores todo information
    * `EventStorage`: Manages event data
    * `UserPrefsStorage`: Handles user preferences

The `LogicManager` interacts with the Model component to execute commands on the various data managers, and with the Storage component to persist any changes to the appropriate storage locations.

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2425S2-CS2103-F08-4/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g. `CommandBox`, `ResultDisplay`, `ListPanel` etc. All
these UI components inherit from the abstract `UiPart<T>` class which captures the commonalities between classes that
represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that
are in the `src/main/resources/view` folder. For example, the layout of the [
`MainWindow`](https://github.com/AY2425S2-CS2103-F08-4/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java)
is specified in [
`MainWindow.fxml`](https://github.com/AY2425S2-CS2103-F08-4/tp/tree/master/src/main/resources/view/MainWindow.fxml).

The UI employs a flexible design pattern through several key components:

1. **Card System**: The UI uses a card-based display system:
    - The `Card<T>` interface defines how entity information is displayed
    - `YCard` classes (like `EventCard`) implement this interface and extend `UiPart<Region>`
    - The `CardFactory<T>` interface and its implementation `GenericCardFactory<T>` are responsible for creating
      appropriate cards for different entity types

2. **Display Adapters**:
    - The `DisplayableItem` interface in the util package defines how items can be displayed in the UI
    - The abstract `ItemAdapter<T>` class provides a foundation for adapting model entities to the UI
    - `GenericAdapter<T>` implements this pattern, allowing model items to be wrapped for display

3. **List Display**:
    - `ListPanel` uses `DisplayableListViewCell` (which extends `UiPart`) to render items
    - Each cell renders a `DisplayableItem` by calling its `getDisplayCard()` method
    - The `MainWindow` has two separate `ListPanel` instances for different types of content

The `UiManager` (which implements the `Ui` interface) is responsible for initializing all UI components and handling
JavaFX lifecycle events. It maintains a reference to the `Logic` component to execute commands and observe model
changes.

These relationships form a clear separation of concerns:

- `YCard` references model entity `Y` directly to display its properties
- `DisplayableListViewCell` renders any `DisplayableItem` in a type-safe manner
- `Card` interface provides a consistent way to get the underlying `UiPart`
- `ItemAdapter` uses a `CardFactory` to create appropriate cards

The `UI` component:

* Executes user commands using the `Logic` component
* Keeps a reference to the `Logic` component to execute commands
* Listens for changes to `Model` data to update the UI accordingly
* Depends on classes in the `Model` component (through direct references and adapter patterns)
* Uses event handling for user interactions through click handlers assigned to list items
* Supports different view modes that can be toggled in the `MainWindow`
* Uses `GenericCardFactory` and `GenericAdapter` to handle model items in a type-safe way

### Logic component

**API** : [`Logic.java`](https://github.com/AY2425S2-CS2103-F08-4/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("contact delete
1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `contact delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `ContactCommandParser`, `DeleteContactCommandParser`), should end at
the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `ParserImpl` object which in turn cascades
   through the parser hierarchy (item -> operation) to find a parser that matches the command (e.g.,
   `DeleteContactCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g.
   `DeleteContactCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a contact).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `ParserImpl` class creates an `XYCommandParser` (`XY` is a
  placeholder for the specific command and item name e.g., `AddContactCommandParser` as mentioned above) which uses the other classes
  shown above to parse the user command and create a `XYCommand` object (e.g., `AddContactCommand`) which the
  `ParserImpl` returns back as a `Command` object.
* All `XYCommandParser` classes (e.g., `AddContactCommandParser`, `DeleteContactCommandParser`, ...) inherit from the
  `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2425S2-CS2103-F08-4/tp/blob/master/src/main/java/seedu/address/model/Model.java)


The `Model` component,

* stores the data, i.e., all `Contact`, `Todo`, and `Event` objects (which are contained in separate `UniqueItemList` objects).
* stores the currently 'selected' objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

The diagram below shows how the APIs of the `Model` component are exposed.

<puml src="diagrams/ModelInterfaces.puml" width="450" />

Storing `Contact`, `Todo`, and `Event` share many similarities, so we extract common methods into abstract classes. Yes, we know that we are geniuses at naming things.

<puml src="diagrams/ManagerClassDiagram.puml" width="1000" />

The diagram below shows how a `Contact` is managed by the `Model`.

<puml src="diagrams/ContactManagerClassesDiagram.puml" width="450" />

The diagram below shows how a `Todo` is managed by the `Model`. An `Event` is managed in a similar way.

<puml src="diagrams/TodoManagerClassesDiagram.puml" width="450" />

The diagram below shows different relationship between different `Item`s managed by the `Model`.

<puml src="diagrams/ItemClassDiagram.puml" width="450" />

### Storage component

**API** : [`Storage.java`](https://github.com/AY2425S2-CS2103-F08-4/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both item data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from all of `ContactStorage`, `TodoStorage`, `EventStorage`, and `UserPrefStorage`, which means it can be treated as either one of them (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`). Notably, `TodoStorage` and `EventStorage` depend on an `ItemNotInvolvingContactManager<Contact>` to validate contacts' IDs and get the corresponding `Contact`s.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Implemented\] New command execution feature

#### Implementation

The command execution feature is a core component of the application that processes user inputs and executes the corresponding commands. The sequence diagram below illustrates the execution flow when a user enters a command:

<puml src="diagrams/XYCommandSequenceDiagram.puml" width="450" />

The command execution process involves several key steps:

1. The process begins when the user enters a command through the UI
2. `MainWindow` forwards the command text to `LogicManager` for execution
3. `LogicManager` passes the command to `ParserImpl` for parsing
4. `ParserImpl` identifies the command type (in this case, a `Y` command with `X` subcommand)
5. The command is then forwarded to the appropriate command parser (`XYCommandParser`)
6. `XYCommandParser` uses the `ArgumentTokenizer` to tokenize the input and extract arguments
7. Arguments are validated and converted to the appropriate types using `ParserUtil`
8. An `XYCommand` object is created with the parsed arguments and returned to `LogicManager`
9. `LogicManager` executes the command, which typically involves updating the model (e.g., filtering items)
10. After execution, the model changes are saved to storage and a `CommandResult` is returned to `MainWindow`

This architecture follows the Command pattern, allowing for extendable command functionality while maintaining a clean separation between the UI and the application logic.

#### Design considerations:

* **Alternative 1 (current choice):** Use a centralized command execution flow with specialized parsers.
    * Pros:
        * Clear separation of concerns with dedicated parser for each command type
        * Easily extensible for new commands
        * Consistent command execution pattern
        * Strong encapsulation of command execution logic
    * Cons:
        * Slightly more complex class structure
        * Additional overhead for simple commands

* **Alternative 2:** Process commands directly in the logic component without specialized parsers.
    * Pros:
        * Simpler implementation for basic commands
        * Fewer objects created during command execution
    * Cons:
        * Less maintainable as the application grows
        * Harder to extend with new commands
        * Potential duplication of parsing logic
        * Reduced testability of individual components

### \[Implemented\] GUI `Y` card click action alias to `Y info <idx>`

#### Implementation

When a user clicks on a card in the UI, the application automatically executes a `Y info <index>` command
to display detailed information about the selected contact. The sequence diagram below illustrates this process:

<puml src="diagrams/AliasXInfoSequenceDiagram.puml" width="450" />

The process begins when a user clicks on a `YCard` in the UI:

1. The `YCard` has a click handler that was set up when the card was created
2. When clicked, this handler is executed and passed to the `ListPanel` that contains the card
3. The `ListPanel` forwards the index of the clicked card to `MainWindow` through the `onItemClickHandler` consumer
4. `MainWindow` processes this by generating a command string in the format `Y info <index>`
5. This command string is passed to `LogicManager` for execution, which processes it through the command execution
   pipeline
6. After execution, the `CommandResult` is returned to `MainWindow`
7. `MainWindow` updates the `ResultDisplay` to show feedback to the user

This implementation allows users to quickly access detailed contact information with a single click rather than having
to manually type the `Y info <index>` command.

#### Design considerations:

* **Alternative 1 (current choice):** Translate UI interactions into command strings.
    * Pros:
        * Maintains a single execution pathway for commands regardless of how they're initiated
        * Each UI interaction can be modeled as a specific command
        * Simplifies testing as UI interactions and command-line inputs use the same logic paths
    * Cons:
        * Adds overhead of command string creation and parsing for simple UI interactions

* **Alternative 2:** Create a separate pathway for UI-triggered events.
    * Pros:
        * Potentially more efficient for simple actions
        * Can be optimized for specific UI interactions
    * Cons:
        * Creates duplicate logic paths for the same functionality
        * More difficult to maintain consistency between command-line and UI-triggered actions
        * Increases complexity of testing

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedTutorConnect`. It extends `TutorConnect` with an undo/redo history, stored internally as an `TutorConnectStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedTutorConnect#commit()` — Saves the current TutorConnect state in its history.
* `VersionedTutorConnect#undo()` — Restores the previous TutorConnect state from its history.
* `VersionedTutorConnect#redo()` — Restores a previously undone TutorConnect state from its history.

These operations are exposed in the `Model` interface as `Model#commitTutorConnect()`, `Model#undoTutorConnect()` and `Model#redoTutorConnect()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedTutorConnect` will be initialized with the initial TutorConnect state, and the `currentStatePointer` pointing to that single TutorConnect state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `contact delete 5` command to delete the 5th contact in TutorConnect. The `contact delete` command calls `Model#commitTutorConnect()`, causing the modified state of TutorConnect after the `contact delete 5` command executes to be saved in the `TutorConnectStateList`, and the `currentStatePointer` is shifted to the newly inserted TutorConnect state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `contact add --name David …​` to add a new contact. The `contact add` command also calls `Model#commitTutorConnect()`, causing another modified TutorConnect state to be saved into the `TutorConnectStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitTutorConnect()`, so TutorConnect state will not be saved into the `TutorConnectStateList`.

</box>

Step 4. The user now decides that adding the contact was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoTutorConnect()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous TutorConnect state, and restores TutorConnect to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial TutorConnect state, then there are no previous TutorConnect states to restore. The `undo` command uses `Model#canUndoTutorConnect()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoTutorConnect()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores TutorConnect to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `TutorConnectStateList.size() - 1`, pointing to the latest TutorConnect state, then there are no undone TutorConnect states to restore. The `redo` command uses `Model#canRedoTutorConnect()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `contact list`. Commands that do not modify TutorConnect, such as `contact list`, will usually not call `Model#commitTutorConnect()`, `Model#undoTutorConnect()` or `Model#redoTutorConnect()`. Thus, the `TutorConnectStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `contact clear`, which calls `Model#commitTutorConnect()`. Since the `currentStatePointer` is not pointing at the end of the `TutorConnectStateList`, all TutorConnect states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `contact add --name David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire TutorConnect.
    * Pros: Easy to implement.
    * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
    * Pros: Will use less memory (e.g. for `delete`, just save the contact being deleted).
    * Cons: We must ensure that the implementation of each individual command are correct.

### \[Implemented\] Command History Navigation

#### Implementation

The application supports command history navigation, allowing users to quickly recall and reuse previously entered commands. This feature is implemented in the `CommandBox` component, which is responsible for capturing and processing user command inputs.

The command history is stored as a list of strings, with a pointer (index) that keeps track of the current position when navigating through the history. The sequence diagram below illustrates how command history navigation works:

<puml src="diagrams/CommandHistorySequenceDiagram.puml" width="250" />

The command history navigation flow follows these steps:

1. When a user presses the Up or Down arrow key in the command text field, the `handleKeyPress` method in `CommandBox` captures this event
2. For Up arrow key presses, the `displayPreviousCommand` method is called, which:
    - Decrements the current command index if it's not already at the beginning of the history
    - Retrieves the command at the new index
    - Updates the command text field to display this command
3. For Down arrow key presses, the `displayNextCommand` method is called, which:
    - Increments the current command index if it's not already at the end of the history
    - If at the end of history, clears the command text field
    - Otherwise, displays the command at the new index

The state diagram below captures the possible states and transitions during command history navigation:

<puml src="diagrams/CommandHistoryStateDiagram.puml" width="250" />

When a new command is entered, it's added to the history list, and the index is reset to point to the end of the list. This ensures that pressing the Up arrow immediately after entering a command will display the most recently entered command.

#### Design considerations:

* **Alternative 1 (current choice):** Store complete command history for the session.
    * Pros:
        * Users can access all commands they've entered during the session
        * Simple implementation with a list and index pointer
        * Familiar behavior matching most command-line interfaces
    * Cons:
        * Memory usage increases with session length
        * No persistence between application sessions

* **Alternative 2:** Limit history to a fixed number of most recent commands.
    * Pros:
        * Bounded memory usage
        * More efficient navigation through a smaller set of commands
    * Cons:
        * Limited access to older commands
        * Additional complexity to maintain the limited history

* **Alternative 3:** Add persistence for command history between sessions.
    * Pros:
        * Commands available across application restarts
        * Enhanced user experience for recurring tasks
    * Cons:
        * Additional complexity for storage and retrieval
        * Potential privacy concerns for sensitive commands

### \[Implemented\] Command Parser with flags
The proposed parsing mechanism for `Command` containing flags is facilitated by specialized `XYCommandParser` classes. These parsers interpret user input and construct the appropriate `XYCommand` objects. The parsing process relies on several utility components:

Where `X` refers to the classes with the `item` interface. `Y` refers to the new feature related to the class `X`.

* `ArgumentTokenizer` – splits the raw input into prefixes and arguments.

* `ArgumentMultimap` – maps each prefix to its respective value(s).

The following sequence diagram shows how an edit operation goes through the Logic component:

<puml src="diagrams/XYCommandSequenceDiagram.puml" width="250" />

Step 1. The `args` for `XYCommand` gets passed into the corresponding `XYCommandParser`.

Step 2: The `ArgumentTokenizer` scans the raw input string and splits it based on the defined prefixes (e.g., `--tag` for tags, `--name` for names).

Each prefix and its value(s) are stored in an `ArgumentMultimap`. Internally, the following methods are used:

* `ArgumentTokenizer#findAllPrefixPositions()`: Collects all positions of the supplied prefixes in the `argsString`.
* `ArgumentTokenizer#findPrefixPositions()`: Iteratively searches the string for instances of a specific prefix (e.g., `--tag`, `--name`).
* `ArgumentMultimap#extractArguments()`: Once all prefix positions are gathered, `extractArguments()` slices up the original string based on the positions and maps each prefix to its corresponding value(s). The final result is stored in an `ArgumentMultimap`.

Step 3: After tokenization, the `ArgumentMultimap` contains all parsed arguments grouped by their corresponding prefixes.

This step involves:

Checking for required arguments:
Certain prefixes (e.g., `PREFIX_NAME`) may be mandatory. If missing, a `ParseException` is thrown.

Converting strings into domain-specific objects:

* `ParserUtil.parseName(...)` for `names`

* `ParserUtil.parseTags(...)` for `tags`

* `ParserUtil.parseIndex(...)` for `indices`

These utility methods ensure type safety and can be defined as you needed. If any value fails to conform to the expected format (e.g., an invalid index or empty tag), a `ParseException` is raised with an appropriate error message.

Step 4: Once all necessary values are extracted and validated, the final command is constructed:

At this stage, the parser assembles the required information into an executable `XYCommand` object, which is returned to the `LogicManager`.

Design Considerations
* Modularity: Each command has its own parser class, keeping logic isolated and maintainable.

* Reusability: Shared utility classes like `ArgumentTokenizer`, `ArgumentMultimap`, and `ParserUtil` ensure consistency across parsers.

* Scalability: Easily supports optional, repeatable, and order-independent arguments through prefix-based parsing. These prefixes 
are called and parsed by `ArgumentTokenizer` as required.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Tutors managing student information and course-related details
  * Needs to manage course tasks related to a student or tutor.
  * Needs to track student attendance of classes.
* Needs to manage a significant number of contacts
* Prefers desktop apps over other types
* Can type fast
* Prefers typing to mouse interactions
* Is reasonably comfortable using CLI apps

**Value proposition**: Tutors often teach multiple courses across different platforms. Without being a full-fledged learning management system, TutorConnect simplifies student management by providing an efficient central platform for storing and organizing student contacts, assigning tasks, and optimizing workflows with intuitive CLI options.

### User stories

Note: Not all features are implemented. The [user guide](UserGuide.md) documents the current set of supported features. This is a work in progress.

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ...                 | I want to ...                            | so that I can ...                                   |
|----------|--------------------------|------------------------------------------|-----------------------------------------------------|
| `* * *`  | first time user          | see a functional help page               | understand the app's functionalities                |
| `* * *`  | tutor                    | automatically load in data               | easily reuse the application                        |
| `* * *`  | tutor                    | automatically save and export data       | easily reuse the application                        |
| `* * *`  | tutor                    | exit the program safely                  | avoid corrupting my files                           |
| `* * *`  | tutor                    | create an event                          | represent a tutorial                                |
| `* * *`  | tutor                    | delete an event                          | remove events I no longer need                      |
| `*`      | impatient tutor          | create recurring events                  | save time and ensure consistency                    |
| `*`      | impatient tutor          | mass import class timings (events)       | save time                                           |
| `* * *`  | tutor                    | add students to an event                 | assign students to class                            |
| `* * *`  | tutor                    | remove students from an event            | unassign students to class                          |
| `* * *`  | tutor                    | log a student as having attended event   | mark attendance                                     |
| `* * *`  | tutor                    | unlog a student as having attended event | account for mistakes in attendance                  |
| `* * *`  | tutor                    | add a contact                            | store the information of my student                 |
| `* *`    | tutor                    | add multiple contacts with same names    | handle students with duplicate names                |
| `* * *`  | tutor                    | delete a contact                         | remove students I no longer need to handle          |
| `* *`    | tutor with many classes  | associate a contact with a class         | remember student is in which class                  |
| `* *`    | tutor with many course   | associate a contact with a course        | remember student is in which course                 |
| `*`      | impatient tutor          | mass import students data                | save time                                           |
| `* *`    | tutor                    | search for a specific contact by feature | retrieve full information for a particular contact  |
| `* *`    | tutor                    | list all contacts                        | see every contact                                   |
| `* *`    | tutor with many classes  | filter all students by class/course      | find students easily for various purposes           |
| `* *`    | tutor                    | label students                           | identify struggling students                        |
| `* *`    | caring tutor             | create a todo                            | represent a task for some contact                   |
| `* *`    | caring tutor             | add contact to todo                      | handle a situation for some contact(s)              |
| `* *`    | caring tutor             | remove contact from todo                 | account for mistakes in the contacts                |
| `* *`    | caring tutor             | mark todo as done                        | remember that I have handled the situation          |
| `* *`    | caring tutor             | mark todo as not done                    | account for mistakes in handling the situation      |
| `*`      | head tutor               | distinguish students and tutors          | add tutors as contacts                              |
| `*`      | head tutor               | apply labels to tutors                   | track tutor performance                             |
| `*`      | morally upright tutor    | tag students suspected of plagiarism     | later report them for further investigation         |
| `*`      | tutor                    | send messages to individuals or groups   | remind them of tasks                                |
| `*`      | tutor teaching many sems | archive old classes                      | retain useful data while focusing on improvements   |
| `*`      | tutor teaching many sems | archive or purge old contacts            | avoid confusion between current and former students |
| `*`      | experienced user         | create custom commands/macros            | optimize workflow                                   |
| `*`      | forgetful tutor          | view upcoming tasks in some priority     | prioritize on tasks with nearer deadlines           |
| `*`      | impatient tutor          | synchronize contact labels with events   | avoid manually tagging students                     |
| `*`      | careless tutor           | undo (multiple times)                    | revert to previous state in case of wrong command   |

### Use cases

(For all use cases below, the **System** is the `TutorConnect (TC)` and the **Actor** is the `User`, unless specified otherwise)

**Use case 1: Create a contact**

**MSS**
1. User requests to create a new contact and provides contact details.
2. TC creates a new contact and adds it to the contact list.
3. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 1a. The given contact ID is a duplicate.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.
* 1b. The given contact ID is invalid.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.
* 1c. The given contact name is invalid.
    * 1c1. TC displays an error message.

      Use case resumes at step 1.
* 1d. The given contact email is invalid.
    * 1d1. TC displays an error message.

      Use case resumes at step 1.
* 1e. The given contact course is invalid.
    * 1e1. TC displays an error message.

      Use case resumes at step 1.
* 1f. The given contact group is invalid.
    * 1f1. TC displays an error message.

      Use case resumes at step 1.
* 1g. The given tags are invalid.
    * 1g1. TC displays an error message.

      Use case resumes from step 1.

**Use case 2: List full information of a contact**

**MSS**
1. User <u>finds all contacts (UC:3)</u>.
2. User requests to retrieve full information of a contact by index.
3. TC displays full information of the contact.

   Use case ends.

**Extensions**
* 2a. The given contact index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given contact index is out of range in the contact list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 3: List all contacts**

**MSS**
1. User requests to view all contacts.
2. TC displays a list of all contacts.

   Use case ends.

**Use case 4: Filter all contacts using some identifiable feature**

**MSS**
1. User requests to filter all contacts using some specific criteria.
2. TC displays the matching contacts along with the number of results.

   Use case ends.

**Extensions**
* 1a. The criteria include a filter with an invalid or empty value.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.

* 1b. The criteria contains no filter.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 5: Edit a contact's details**

**MSS**
1. User <u>finds full information of a contact (UC:2)</u>
2. User requests to edit some fields of the contact.
3. TC updates those fields of the contact.
4. TC displays a confirmation message

   Use case ends.

**Extensions**
* 2a. User does not provide any field to edit.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The field's detail is invalid.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 6: Add a tag to a contact**

**MSS**
1. User requests to add a tag to the contact by index.
2. TC updates the contact with the provided tag.
3. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 1a. The given contact index is not a positive integer.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.
* 1b. The given contact index is out of range in the contact list.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.
* 1c. The given tags are invalid.
    * 1c1. TC displays an error message.

      Use case resumes at step 1.

**Use case 7: Remove a tag from a contact**

**MSS**
1. User <u>finds all tags associated with a contact (UC:2)</u>.
2. User requests to remove a tag from the contact by index.
3. TC updates the contact by removing the provided tag.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given contact index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given contact index is out of range in the contact list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given tags are invalid.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. One of the given tag does not exist in the contact.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.

**Use case 8: Delete a contact**

**MSS**
1. User <u>finds all contacts (UC:3)</u>.
2. User requests to delete the contact by index.
3. TC deletes the contact from the system.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given contact index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given contact index is out of range in the contact list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 9: Delete all contacts**

**MSS**
1. User requests to delete all contacts.
2. TC deletes all contacts in the contact list.
3. TC displays a confirmation message.

   Use case ends.

**Use case 10: Create a todo**

**MSS**
1. User requests to create a todo and provides todo details.
2. TC creates the todo and adds it to the todo list.
3. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 1a. The given name is invalid.
    * 1a1. TC displays an error message.

      Use case resumes from step 1.
* 1b. A todo with the same name already exists.
    * 1b1. TC displays an error message.

      Use case resumes from step 1.
* 1c. The given deadline is invalid.
    * 1c1. TC displays an error message.

      Use case resumes from step 1.
* 1d. The given deadline is invalid.
    * 1d1. TC displays an error message.

      Use case resumes from step 1.
* 1e. The given location is invalid.
    * 1e1. TC displays an error message.

      Use case resumes from step 1.
* 1f. The given tags are invalid.
    * 1f1. TC displays an error message.

      Use case resumes from step 1.

**Use case 11: List full information of a todo**

**MSS**
1. User <u>finds all todos (UC:12)</u>.
2. User requests to retrieve full information of the todo by index.
3. TC displays full information of the todo.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 12: List all todos**

**MSS**
1. User requests to view all todos.
2. TC displays all todos.

   Use case ends.

**Use case 13: Filter all todos using some identifiable feature**

**MSS**
1. User requests to filter all todos using some specific criteria.
2. TC displays the matching todos along with the number of results.

   Use case ends.

**Extensions**
* 1a. The criteria include a filter with an invalid or empty value.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.

* 1b. The criteria contains no filter.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 14: Edit a todo's details**

**MSS**
1. User <u>finds full information of a todo (UC:11)</u>.
2. User requests to edit some fields of the todo.
3. TC updates those fields of the todo.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. User does not provide any field to edit.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The field's detail is invalid.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 15: Add a tag to a todo**

**MSS**
1. User requests to add a tag to the todo by index.
2. TC updates the todo with the provided tag.
3. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 1a. The given todo index is not a positive integer.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.
* 1b. The given todo index is out of range in the todo list.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.
* 1c. The given tags are invalid.
    * 1c1. TC displays an error message.

      Use case resumes at step 1.

**Use case 16: Remove a tag from a todo**

**MSS**
1. User <u>finds all tags associated with a todo (UC:11)</u>.
2. User requests to remove a tag from the todo by index.
3. TC updates the todo by removing the provided tag.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given tags are invalid.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. One of the given tag does not exist in the contact.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.

**Use case 17: Add a contact to a todo**

**MSS**
1. User <u>finds all contacts (UC:3)</u>.
2. User requests to add the contact to a todo by todo index and contact index.
3. TC associates the contact with the todo.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given contact index is not a positive integer.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. The given contact index is out of range in the contact list.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.
* 2e. The contact is already assigned to the todo.
    * 2e1. TC displays an error message.

      Use case resumes at step 2.

**Use case 18: Remove a contact from a todo**

**MSS**
1. User <u>finds all contacts associated with a todo (UC:11)</u>.
2. User requests to remove a contact from a todo by todo index and contact index.
3. TC removes the association.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given contact index is not a positive integer.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. The given contact index is out of range in the todo's contact list.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.

**Use case 19: Mark a todo as completed**

**MSS**
1. User <u>finds all todos (UC:12)</u>.
2. User requests to mark a todo as completed by index.
3. TC marks the todo as completed.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The todo is already completed.
    * 2c1. TC displays an error message.

      Use case ends.

**Use case 20: Mark a todo as not completed**

**MSS**
1. User <u>finds all todos (UC:12)</u>.
2. User requests to mark a todo as not completed by index.
3. TC marks the todo as not completed.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The todo is not completed.
    * 2c1. TC displays an error message.

      Use case ends.

**Use case 21: Delete a todo**

**MSS**
1. User <u>finds all todos (UC:12)</u>.
2. User requests to delete a todo by index.
3. TC deletes the todo.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given todo index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given todo index is out of range in the todo list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 22: Delete all todos**

**MSS**
1. User requests to delete all todos.
2. TC deletes all todos in the todo list.
3. TC displays a confirmation message.

   Use case ends.

**Use case 23: Create an event**

**MSS**
1. User requests to create an event and provides event details.
2. TC creates the event and adds it to the event list.
3. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 1a. The given name is invalid.
    * 1a1. TC displays an error message.

      Use case resumes from step 1.
* 1b. An event with the same name already exists.
    * 1b1. TC displays an error message.

      Use case resumes from step 1.
* 1c. The given start datetime is invalid.
    * 1c1. TC displays an error message.

      Use case resumes from step 1.
* 1d. The given end datetime is invalid.
    * 1d1. TC displays an error message.

      Use case resumes from step 1.
* 1e. The given location is invalid.
    * 1e1. TC displays an error message.

      Use case resumes from step 1.
* 1f. The given tags are invalid.
    * 1f1. TC displays an error message.

      Use case resumes from step 1.

**Use case 24: List all events**

**MSS**
1. User requests to view all events.
2. TC displays all events.

   Use case ends.

**Use case 25: List full information of an event**

**MSS**
1. User <u>finds all events (UC:24)</u>.
2. User requests to retrieve full information of the event by index.
3. TC displays full information of the event.

   Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 26: Filter all events using some identifiable feature**

**MSS**
1. User requests to filter all events using some specific criteria.
2. TC displays the matching events along with the number of results.

   Use case ends.

**Extensions**
* 1a. The criteria include a filter with an invalid or empty value.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.

* 1b. The criteria contains no filter.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 27: Edit an event's details**

**MSS**
1. User <u>finds full information of a event (UC:24)</u>.
2. User requests to edit some fields of the event.
3. TC updates those fields of the event.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. User does not provide any fields to edit.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The field's detail is invalid.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 28: Add a tag to an event**

**MSS**
1. User requests to add a tag to the event by index.
2. TC updates the event with the provided tag.
3. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 1a. The given event index is not a positive integer.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.
* 1b. The given event index is out of range in the event list.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.
* 1c. The given tags are invalid.
    * 1c1. TC displays an error message.

      Use case resumes at step 1.

**Use case 29: Remove a tag from an event**

**MSS**
1. User <u>finds all tags associated with an event (UC:25)</u>.
2. User requests to remove a tag from the event by index.
3. TC updates the event by removing the provided tag.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given tags are invalid.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. One of the given tags does not exist in the event.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.

**Use case 30: Add a contact to an event**

**MSS**
1. User <u>finds all contacts (UC:3)</u>.
2. User requests to add the contact to an event by event index and contact index.
3. TC associates the contact with the event.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given contact index is not a positive integer.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. The given contact index is out of range in the contact list.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.
* 2e. The contact is already assigned to the event.
    * 2e1. TC displays an error message.

      Use case resumes at step 2.

**Use case 31: Remove a contact from an event**

**MSS**
1. User <u>finds all contacts associated with an event (UC:25)</u>.
2. User requests to remove a contact from an event by event index and contact index.
3. TC removes the association.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given contact index is not a positive integer.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. The given contact index is out of range in the event's contact list.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.

**Use case 32: Log a contact as having attended an event**

**MSS**
1. User <u>finds all contacts associated with an event (UC:25)</u>.
2. User requests to log a contact as having attended for an event by event index and contact index.
3. TC marks the contact as attended.
4. TC displays a confirmation message.

    Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given contact index is not a positive integer.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. The given contact index is out of range in the event's contact list.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.
* 2e. The given contact has already been logged as having attended the event.
    * 2e1. TC displays an error message.

      Use case ends.

**Use case 33: Log a contact as not having attended an event**

**MSS**
1. User <u>finds all contacts associated with an event (UC:25)</u>.
2. User requests to log a contact as not having attended for an event by event index and contact index.
3. TC marks the contact as not attended.
4. TC displays a confirmation message.

    Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.
* 2c. The given contact index is not a positive integer.
    * 2c1. TC displays an error message.

      Use case resumes at step 2.
* 2d. The given contact index is out of range in the event's contact list.
    * 2d1. TC displays an error message.

      Use case resumes at step 2.
* 2e. The given contact is already marked as not having attended the event.
    * 2e1. TC displays an error message.

      Use case ends.

**Use case 34: Delete an event**

**MSS**
1. User <u>finds all events (UC:24)</u>.
2. User requests to delete an event by index.
3. TC deletes the event.
4. TC displays a confirmation message.

   Use case ends.

**Extensions**
* 2a. The given event index is not a positive integer.
    * 2a1. TC displays an error message.

      Use case resumes at step 2.
* 2b. The given event index is out of range in the event list.
    * 2b1. TC displays an error message.

      Use case resumes at step 2.

**Use case 35: Delete all events**

**MSS**
1. User requests to delete all events.
2. TC deletes all events in the event list.
3. TC displays a confirmation message.

   Use case ends.

**Use case 36: List all commands by item**

**MSS**
1. User requests to list all commands of an item.
2. TC displays all commands of that item.

   Use case ends.

**Extensions**
* 1a. The given item is unrecognized (i.e. Not a contact, todo or event).
    * 1a1. TC displays an error message.

      Use case resumes at step 1.

**Use case 37: List help message of a specific command**

**MSS**
1. User requests to see help message of a command of a specific item.
2. TC displays the help message.

   Use case ends.

**Extensions**
* 1a. The given item is unrecognized (i.e. Not a contact, todo or event).
    * 1a1. TC displays an error message.

      Use case resumes at step 1.
* 1b. The given command is unrecognized.
    * 1b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 38: Exit the program**

**MSS**
1. User requests to exit the program.
2. TC exits the program.

   Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 contacts without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should not lose more than 1-minute worth of data in the event of a crash or unexpected shutdown.
5.  Should be easily testable to ensure that new updates or features do not negatively impact existing functionality.
6.  Should function without requiring an installer.
7.  Should only use third-party libraries or services that are free, open-source, have permissive license terms, and do not require installation by the user.
8.  Should display optimally on screen resolutions 1920x1080 and higher with screen scales 100% and 125%, and remain functional on 1280x720 and higher with up to 150% screen scaling.
9.  The main application (JAR/ZIP file) should not exceed 100MB.

### Glossary

* **Contact**: A stored record of a student or tutor, containing relevant details such as name, ID, and class.
* **Class**: A group of students assigned to a particular tutor.
* **Course**: A subject or academic course that multiple classes and students may belong to.
* **Event**: A scheduled session such as tutorial class, remedial, or consultation that tutors can create, modify, and assign students to.
* **Todo**: A task or action item that can be associated with a student or another tutor, such as grading assignments, scheduling follow-ups, or preparing lesson materials.
* **Tag**: A specific keyword which can be associated with an arbitrary value, for a specific contact, event, or todo.
* **Head Tutor**: A tutor responsible for overseeing other tutors.
* **Mainstream OS**: Windows, Linux, Unix, MacOS.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file.<br>
      Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
      Expected: The most recent window size and location is retained.


### Deleting a contact

1. Deleting a contact while all contacts are being shown

   1. Prerequisites: List all contacts using the `contact list` command. Multiple contacts in the list.

   1. Test case: `contact delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message.

   1. Test case: `contact delete 0`<br>
      Expected: No contact is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `contact delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Manual modification of data

**Tip**: Refer to the [user guide](UserGuide.md) for examples of valid and invalid parameters.

1. Dealing with missing data files

   1. Delete the `data` folder, or any json files within it.
   2. Re-launch the app.<br>
      Expected: The app starts up with a sample list for each missing file.
   3. Exit the app via any valid exit command (note: closing the window or terminating the process is not a valid exit command).<br>
      Expected: The data files are saved in the `data` folder upon execution of any valid command.

1. Dealing with corrupted data files
   1. Ensure that sample data files exist in the `data` folder (see previous point).
   1. Test case 1: Replace the value of any field within any json file with an invalid value, then re-launch the app.<br>
      Expected: The specific entry is skipped and the rest of the contents are loaded. 
   1. Test case 2: Make major incorrect corruption to any json file, then re-launch the app.<br>
      Expected: All invalid entries are skipped and the rest of the contents are loaded (if any).

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Planned Enhancements**
Team size: 5
1. **Make delete command support multiple indices:** Extend support to delete multiple items at once, similar to tagging and linking contacts.
2. **Safeguard users against major irreversible actions:** For example, clearing contacts is presently an irreversible action. Two possible directions include seeking confirmation from the user, or supporting an undo feature to revert any changes.
3. **Support more operators in `filter` command:** As `filter` command is targeted at advanced users (power users), other operators (e.g. `xor:`) can be added to support more complex queries, and queries for optional values such as tags should be expanded to support searching for no values.
4. **Support operators across criteria in `filter` command**: `filter` is currently limited to applying `and` across all criteria (i.e. must satisfy all criteria). Much like operators expand the functionality of a single criterion, operators across criteria can further enhance the functionality of `filter` command.

## **Appendix: Effort**

Difficulty level is on the higher end due to the large number of features that were implemented, alongside a single complex feature (`filter`).
Special efforts were made to improve the code quality via abstraction of the commonalities between `contact`, `todo` and `event` via `item`.
This choice has resulted in greater consistency of behavior across the three types, in terms of the input restrictions, error behaviors and output formats, which were implemented concretely via the definitions of numerous interfaces and refactoring of existing codebase which was originally targeted at the `contact` type.
A particularly challenging aspect is the curation of the `filter` command, from the set of allowed operations to the syntax that the user can input. The aim was to create a powerful and easy to use command that is aimed at advanced users.
