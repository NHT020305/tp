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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [
`Ui.java`](https://github.com/AY2425S2-CS2103-F08-4/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

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
    - `XCard` classes (like EventCard) implement this interface and extend `UiPart<Region>`
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

- `XCard` references model entity `X` directly to display its properties
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

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("contact delete 
1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

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
* When called upon to parse a user command, the `ParserImpl` class creates an `XYZCommandParser` (`XYZ` is a 
  placeholder for the specific command and item name e.g., `AddContactCommandParser`) which uses the other classes 
  shown above to parse the user command and create a `XYZCommand` object (e.g., `AddContactCommand`) which the 
  `ParserImpl` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddContactCommandParser`, `DeleteContactCommandParser`, ...) inherit from the 
  `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)


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

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

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

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th contact in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new contact. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the contact was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the contact being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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
  * Needs to track student attendance and student issues
  * Needs to manage course related tasks
* Has a need to manage a significant number of contacts
* Prefers desktop apps over other types
* Can type fast
* Prefers typing to mouse interactions
* Is reasonably comfortable using CLI apps

**Value proposition**: Tutors often teach multiple courses across different platforms. Without being a full-fledged learning management system, TutorConnect simplifies student management by providing an efficient central platform for storing and organizing student contacts, assigning tasks, and optimizing workflows with intuitive CLI options.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a ...                 | I want to ...                                  | so that I can ...                                   |
|----------|--------------------------|------------------------------------------------|-----------------------------------------------------|
| `* * *`  | first time user          | see a functional help page                     | understand the app's functionalities                |
| `*`      | first-time user          | get a guide to creating my profile             | know what to do and not feel overwhelmed            |
| `* * *`  | tutor                    | automatically load in data                     | easily reuse the application                        |
| `* * *`  | tutor                    | automatically save and export data             | easily reuse the application                        |
| `* * *`  | tutor                    | safely exit the program                        | avoid corrupting my files                           |
| `* * *`  | tutor                    | create an event                                | represent a tutorial                                |
| `* * *`  | tutor                    | delete an event                                | remove events I no longer need                      |
| `*`      | impatient tutor          | create recurring events                        | save time and ensure consistency                    |
| `*`      | impatient tutor          | mass import class timings (events)             | save time                                           |
| `* * *`  | tutor                    | add students to an event                       | assign students to class                            |
| `* * *`  | tutor                    | remove students from an event                  | unassign students to class                          |
| `* * *`  | tutor                    | log a student student as having attended event | check attendance                                    |
| `* * *`  | tutor                    | add a contact                                  | store the information of my student                 |
| `* *`    | tutor                    | add multiple contacts with same names          | handle students with duplicate names                |
| `* * *`  | tutor                    | delete a contact                               | remove students I no longer need to handle          |
| `* *`    | tutor with many classes  | label a contact with a class                   | remember student is in which class                  |
| `* *`    | tutor with many course   | label a contact with a course                  | remember student is in which course                 |
| `*`      | impatient tutor          | mass import students data                      | save time                                           |
| `* *`    | tutor                    | search for a specific contact by feature       | retrieve full information for a particular contact  |
| `* *`    | tutor                    | list all contacts                              |                                                     |
| `* *`    | tutor with many classes  | filter all students by class/course            | find students easily for various purposes           |
| `* *`    | tutor with many classes  | sort all students by class/course              | find students easily for various purposes           |
| `* *`    | tutor                    | label progress of students                     | identify struggling students                        |
| `* *`    | caring tutor             | create a todo                                  | represent a task for some contact                   |
| `* *`    | caring tutor             | add contact to todo                            | handle a situation for some contact(s)              |
| `* *`    | caring tutor             | remove contact from todo                       |                                                     |
| `* *`    | caring tutor             | mark todo as done                              | remember that I have handled the situation          |
| `* *`    | caring tutor             | mark todo as not done                          |                                                     |
| `*`      | head tutor               | distinguish students and tutors                | add tutors as contacts                              |
| `*`      | head tutor               | know when my tutors are unavailable            | schedule make-up classes and track tutor attendance |
| `*`      | head tutor               | apply labels to tutors                         | track tutor performance                             |
| `*`      | morally upright tutor    | tag students suspected of plagiarism           | later report them for further investigation         |
| `*`      | tutor                    | send messages to individuals or groups         | remind them of tasks                                |
| `*`      | tutor teaching many sems | archive old classes                            | retain useful data while focusing on improvements   |
| `*`      | tutor teaching many sems | archive or purge old contacts                  | avoid confusion between current and former students |
| `*`      | experienced user         | create custom commands/macros                  | optimize workflow                                   |
| `*`      | forgetful tutor          | view upcoming tasks in some priority           | prioritize on tasks with nearer deadlines           |
| `*`      | impatient tutor          | synchronize contact labels with events         | avoid manually tagging students                     |
| `*`      | careless tutor           | undo (multiple times)                          | revert to previous state in case of wrong command   |

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

**Extensions**

**Use case 4: Filter all contacts using some identifiable feature**

**MSS**
1. User requests to filter all contacts using some specific criteria.
2. TC displays the matching contacts along with the number of results.

   Use case ends.

**Extensions**
* 1a. The criteria include a filter with an empty value.
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

      Use case resumes at step 1.
* 2b. The field's detail is invalid.
    * 2b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 6: Add a tag to a contact**

**MSS**
1. User requests to add a tag to the contact by index.
2. TC updates the contact with the provided tag.
3. TC displays a confirmation message.

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
* 1a. The criteria include a filter with an empty value.
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

      Use case resumes at step 1.
* 2b. The field's detail is invalid.
    * 2b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 15: Add a tag to a todo**

**MSS**
1. User requests to add a tag to the todo by index.
2. TC updates the todo with the provided tag.
3. TC displays a confirmation message.

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

**Extensions**
* 3a. The given todo index is not a positive integer.
    * 3a1. TC displays an error message.

      Use case resumes at step 3.
* 3b. The given todo index is out of range in the todo list.
    * 3b1. TC displays an error message.

      Use case resumes at step 3.
* 3c. The given contact index is not a positive integer.
    * 3c1. TC displays an error message.

      Use case resumes at step 3.
* 3d. The given contact index is out of range in the contact list.
    * 3d1. TC displays an error message.

      Use case resumes at step 3.
* 3e. The contact is already assigned to the todo.
    * 3e1. TC displays an error message.

      Use case resumes from step 3.

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
2. TC creates the todo and adds it to the event list.
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
* 1a. The criteria include a filter with an empty value.
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

      Use case resumes at step 1.
* 2b. The field's detail is invalid.
    * 2b1. TC displays an error message.

      Use case resumes at step 1.

**Use case 28: Add a tag to an event**

**MSS**
1. User requests to add a tag to the todo by index.
2. TC updates the todo with the provided tag.
3. TC displays a confirmation message.

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

**Extensions**
* 3a. The given event index is not a positive integer.
    * 3a1. TC displays an error message.

      Use case resumes at step 3.
* 3b. The given event index is out of range in the event list.
    * 3b1. TC displays an error message.

      Use case resumes at step 3.
* 3c. The given contact index is not a positive integer.
    * 3c1. TC displays an error message.

      Use case resumes at step 3.
* 3d. The given contact index is out of range in the contact list.
    * 3d1. TC displays an error message.

      Use case resumes at step 3.
* 3e. The contact is already assigned to the event.
    * 3e1. TC displays an error message.

      Use case resumes from step 3.

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

**Extensions**
* 3a. The given event index is not a positive integer.
    * 3a1. TC displays an error message.

      Use case resumes at step 3.
* 3b. The given event index is out of range in the event list.
    * 3b1. TC displays an error message.

      Use case resumes at step 2.
* 3c. The given contact index is not a positive integer.
    * 3c1. TC displays an error message.

      Use case resumes at step 2.
* 3d. The given contact index is out of range in the event's contact list.
    * 3d1. TC displays an error message.

      Use case resumes at step 2.
* 3e. The given contact has already been logged as having attended the event.
    * 3e1. TC displays an error message.

      Use case ends.

**Use case 33: Log a contact as not having attended an event**

**MSS**
1. User <u>finds all contacts associated with an event (UC:25)</u>.
2. User requests to log a contact as not having attended for an event by event index and contact index.
3. TC marks the contact as not attended.
4. TC displays a confirmation message.

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

**Use case 36: List all subcommands by feature**

**MSS**
1. User requests to list all subcommands of a feature.
2. TC displays all subcommands of that feature.

   Use case ends.

**Extensions**
* 1a. The given feature is invalid.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.

**Use case 37: List help message of a subcommand**

**MSS**
1. User requests to see help message of a subcommand of a specific feature.
2. TC displays the help message.

   Use case ends.

**Extensions**
* 1a. The given feature is unrecognized.
    * 1a1. TC displays an error message.

      Use case resumes at step 1.
* 1b. The given subcommand is unrecognized.
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
* **Tag**: A specific keyword which can be associated with an arbitrary value, for a specific contact.
  * **Class**: A group of students assigned to a particular tutor.
  * **Course**: A subject or academic course that multiple classes and students may belong to.
* **Event**: A scheduled session such as tutorial class, remedial, or consultation that tutors can create, modify, and assign students to.
* **Todo**: A task or action item that can be associated with a student or another tutor, such as grading assignments, scheduling follow-ups, or preparing lesson materials.
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

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a contact

1. Deleting a contact while all contacts are being shown

   1. Prerequisites: List all contacts using the `list` command. Multiple contacts in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No contact is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
