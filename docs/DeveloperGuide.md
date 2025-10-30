---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# GreyBook Developer Guide

# Table of Contents
<!-- * Table of Contents -->
<page-nav-print />

---

## **Acknowledgements**

- This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

---

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

---

<div style="page-break-after: always;"></div>

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The **_Architecture Diagram_** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/MainApp.java)) is in charge of the app launch and shut down.

- At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
- At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

- [**`UI`**](#ui-component): The UI of the App.
- [**`Logic`**](#logic-component): The command executor.
- [**`Model`**](#model-component): Holds the data of the App in memory.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

- defines its _API_ in an `interface` with the same name as the Component.
- implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonTablePanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

- executes user commands using the `Logic` component.
- listens for changes to `Model` data so that the UI can be updated with the modified data.
- keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
- depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

How the `Logic` component works:

1. Every command registers the types of arguments they take and the restrictions on those arguments to the `GreyBookParser`. The parser stores them using a `CommandParser` object.
1. When `Logic` is called upon to execute a command, it is passed to an `GreyBookParser` object which in turn gets the corresponding `CommandParser` object.
1. The `CommandParser` object then parses the arguments, and creates a `ArgumentParseResult` object, which gets passed back to `Logic`.
1. `Logic` then calls the `ArgumentParseResult` to execute the command using the parsed arguments. `ArgumentParseResult` calls `execute` method on the corresponding command.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:

1. When the application starts, commands (like `AddCommand`, `DeleteCommand`, etc.) register themselves with the `GreyBookParser`.
1. Each command defines the types of arguments it takes by creating instances of `CommandOption` objects, different command options can specify rules such as whether an argument is optional or can occur multiple times. The parsing rules for each argument is also stored in the `CommandOption` object using methods in `ParserUtil` that extends the functional interface `ArgumentParser`. Lastly, each `CommandOption` has an associated `Prefix`.
1. Each command calls `parser.newCommand(...)`, passing in the `CommandOptions` defined by the command, which creates a single instance of `CommandParser` that is stored by the `GreyBookParser`.
1. When the `LogicManager` calls `GreyBookParser::parse(commandText)`, the `GreyBookParser` first identifies the command word (e.g., "add") and retrieves the associated configured `CommandParser`.
1. The retrieved `CommandParser` uses the `ArgumentTokenizer` (along with the defined `Prefix`es) to break down the arguments into an `ArgumentMultimap`.
1. The `CommandParser` then validates the arguments against the registered `CommandOption` rules (e.g., checking for missing required options or duplicate prefixes).
1. For each argument value, the respective `CommandOption` calls its stored `ArgumentParser` to convert the raw string into the required Java object type (e.g., converting a phone number string into a `Phone` object).
1. Finally, the `CommandParser` wraps the specific command instance (`XYZCommand`) and all the parsed values into an `ArgumentParseResult` object, which is returned up the call chain for deferred execution.

<div style="page-break-after: always;"></div>

### Model component

**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

The `Model` component,

- stores the GreyBook data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
- stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
- stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
- does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `GreyBook`, which `Person` references. This allows `GreyBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>

<div style="page-break-after: always;"></div>

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-F13-4/tp/master/src/main/java/greynekos/greybook/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,

- can save both GreyBook data and user preference data in JSON format, and read them back into corresponding objects.
- inherits from both `GreyBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
- depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `greynekos.greybook.commons` package.

---

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedGreyBook`. It extends `GreyBook` with an undo/redo history, stored internally as an `GreyBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

- `VersionedGreyBook#commit()` — Saves the current GreyBook state in its history.
- `VersionedGreyBook#undo()` — Restores the previous GreyBook state from its history.
- `VersionedGreyBook#redo()` — Restores a previously undone GreyBook state from its history.

These operations are exposed in the `Model` interface as `Model#commitGreyBook()`, `Model#undoGreyBook()` and `Model#redoGreyBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedGreyBook` will be initialized with the initial GreyBook state, and the `currentStatePointer` pointing to that single GreyBook state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the GreyBook. The `delete` command calls `Model#commitGreyBook()`, causing the modified state of the GreyBook after the `delete 5` command executes to be saved in the `GreyBookStateList`, and the `currentStatePointer` is shifted to the newly inserted GreyBook state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitGreyBook()`, causing another modified GreyBook state to be saved into the `GreyBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitGreyBook()`, so the GreyBook state will not be saved into the `GreyBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoGreyBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous GreyBook state, and restores the GreyBook to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial GreyBook state, then there are no previous GreyBook states to restore. The `undo` command uses `Model#canUndoGreyBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoGreyBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the GreyBook to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `GreyBookStateList.size() - 1`, pointing to the latest GreyBook state, then there are no undone GreyBook states to restore. The `redo` command uses `Model#canRedoGreyBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the GreyBook, such as `list`, will usually not call `Model#commitGreyBook()`, `Model#undoGreyBook()` or `Model#redoGreyBook()`. Thus, the `GreyBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitGreyBook()`. Since the `currentStatePointer` is not pointing at the end of the `GreyBookStateList`, all GreyBook states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

- **Alternative 1 (current choice):** Saves the entire GreyBook.
  - Pros: Easy to implement.
  - Cons: May have performance issues in terms of memory usage.

- **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  - Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  - Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_

---

## **Documentation, logging, testing, configuration, dev-ops**

- [Documentation guide](Documentation.md)
- [Testing guide](Testing.md)
- [Logging guide](Logging.md)
- [Configuration guide](Configuration.md)
- [DevOps guide](DevOps.md)

---

<div style="page-break-after: always;"></div>

## **Appendix: Requirements**

### Product scope

**Target user profile**:

- has a need to manage club activities and members
- prefer desktop apps over other types
- can type fast
- prefers typing to mouse interactions
- is reasonably comfortable using CLI apps

**Value proposition**: Optimised contact management system for clubs and societies, supporting the administration of common club activities like projects or competitions.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                    | I want to …​                                                                        | So that I can…​                                             |
| -------- | -------------------------- | ----------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| `* * *`  | committee member           | "add a member manually with name contact number email etc"                          | the roster stays up to date.                                |
| `* * *`  | committee member           | I can deactivate (or delete) a member                                               | so that we keep history without cluttering the active list. |
| `* * *`  | committee member           | I can view member details                                                           |                                                             |
| `* * *`  | secretary/attendance taker | "I can mark a member as present absent late or excused"                             | so that attendance status is specific.                      |
| `* *`    | committee member           | "I can assign roles (admin secretary project lead member)"                          | so that permissions are appropriate.                        |
| `* *`    | committee member           | I can restrict access to contact details to authorized roles                        | so that member privacy is protected.                        |
| `* *`    | committee member           | I can edit a member’s details                                                       | so that corrections don’t require creating duplicates.      |
| `* *`    | committee member           | "I can search members by name email tag or year"                                    | so that I can locate people fast.                           |
| `* *`    | secretary/attendance taker | I can create an attendance session with date/time and event name                    | so that attendance is organized by event.                   |
| `* *`    | secretary/attendance taker | I can bulk-mark attendance for selected members                                     | so that I can save time for large events.                   |
| `* *`    | committee member           | I can export attendance for a date range to CSV                                     | so that I can submit readable records to others.            |
| `* *`    | committee member           | I can create a project/competition entry with a title and description               | so that it can be referenced and managed.                   |
| `* *`    | committee member           | I can assign members to a project                                                   | so that teams are clearly defined.                          |
| `* *`    | committee member           | I can archive completed projects                                                    | so that active views remain uncluttered.                    |
| `* *`    | committee member           | I can download the full roster to CSV                                               | so that I can share it with others.                         |
| `* *`    | committee member           | I can generate a report of attendance by month                                      | so that I can review engagement over time.                  |
| `* *`    | committee member           | "I can configure required fields (e.g. emergency contact)"                          | so that we collect essential information.                   |
| `* *`    | committee member           | "I can export an event-day contact sheet (names emergency contacts notes)"          | so that on-site management is safer.                        |
| `* *`    | committee member           | "I can define project-specific custom fields (e.g. competition category team code)" | so that required metadata is captured.                      |
| `* *`    | committee member           | I can track equipment checkout and return by member                                 | so that gear is accounted for.                              |
| `* *`    | committee member           | I can import members from a CSV (or other common formats)                           | so that I can onboard a whole cohort quickly.               |
| `*`      | committee member           | I can merge duplicate member records                                                | so that reports are accurate.                               |
| `*`      | committee member           | "I can tag members with attributes (e.g. role skills year of study)"                | so that I can find suitable members quickly.                |
| `*`      | committee member           | I can see attendance rates per event and per group                                  | so that I can identify engagement trends.                   |
| `*`      | committee member           | I can move a member from one project to another                                     | so that team changes are reflected accurately.              |
| `*`      | committee member           | "I can see a dashboard showing member count active projects and average attendance" | so that I can monitor club health at a glance.              |
| `*`      | committee member           | I can view an audit log of edits to member profiles                                 | so that changes are traceable.                              |
| `*`      | committee member           | "I can create event templates (title location default attendees)"                   | so that recurring events are faster to set up.              |
| `*`      | committee member           | I can auto-assign duties based on availability and past load                        | so that work is distributed fairly.                         |
| `*`      | committee member           | "I can target messages by a saved filter (e.g. year=2 AND skill=web_app)"           | so that only relevant members are contacted.                |
| `*`      | committee member           | I can flag a member as on probation with an expiry date                             | so that restrictions are visible and time-bound.            |
| `*`      | committee member           | "I can log an incident linked to an event (e.g. injury conduct)"                    | so that follow-up is tracked.                               |
| `*`      | committee member           | I can assign a temporary “attendance taker” role for a single event                 | so that volunteers can help without broad access.           |
| `*`      | committee member           | I can freeze an attendance session after review and require a reason to reopen      | so that records are tamper-resistant.                       |
| `*`      | committee member           | "I can set composition caps when forming teams (e.g. max 2 Year-1s)"                | so that rules are enforced automatically.                   |

### Use cases

(For all use cases below, the **System** is the `GreyBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a member**

**MSS**

1.  User requests to add a specific member to the list, providing their name, email, phone number, studentID, and role.
2.  GreyBook adds the member
3.  GreyBook shows a success message

    Use case ends.

**Extensions**

- 1a. The command format is invalid.
  - 1a1. GreyBook shows an error message.
- 1b. The arguments do not follow the correct format.
  - 1b1. GreyBook shows an error message.
- 1c. Another member in the list shares the same studentID
  - 1c1. GreyBook shows an error message.
    Use case resumes at step 2.

**Use case: Delete a member**

**MSS**

1.  User requests to list members
2.  GreyBook shows a list of members
3.  User requests to delete a specific member in the list, providing their index in the list, or their studentID.
4.  GreyBook deletes the member
5.  GreyBook shows a success message

    Use case ends.

**Extensions**

- 2a. The list is empty.

  Use case ends.

- 3a. The given index is invalid.
  - 3a1. GreyBook shows an error message

    Use case resumes at step 2.

- 3b. The given studentID does not exist.
  - 3b1. GreyBook shows an error message, possibly suggests a similar studentID

    Use case resumes at step 2.

**Use Case: Mark Attendance for a member**

**MSS**

1. User requests to list members
2. GreyBook shows a list of members
3. User requests to mark a member's attendance, providing index/studentID and attendance status
4. GreyBook records the attendance status
5. GreyBook shows a success message

Use case ends.

**Extensions**

- 2a. The list is empty.

  Use case ends.

- 3a. The index is invalid
  - 3a1. GreyBook shows an error message

    Use case resumes at step 2.

- 3b. The studentID does not exist in the system.
  - 3b1. GreyBook shows an error message

  Use case resumes at step 2.

- 3c. The attendance status is invalid
  - 3c1. GreyBook shows an error message

    Use case resumes at step 2.

**Use Case: Export Attendance to CSV**

**MSS**

1. User requests to export attendance data
2. GreyBook generates a CSV file with the attendance data
3. GreyBook prompts the user to download the CSV file
4. User downloads the file

Use case ends.

**Extensions**

- 1a. No attendance data exists
  - 1a1. GreyBook shows a info message

    Use case ends

- 4a. Error occurred while trying to write CSV data to file
  - 4a1. GreyBook shows an error message, prompts user to try downloading again

    Use case resumes at step 3.

_{More to be added}_

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The application should efficiently handle and store member and attendance data in a human readable format such as JSON without degrading performance as data grows.

_{More to be added}_

### Glossary

- **Mainstream OS**: Windows, Linux, Unix, MacOS
- **Private contact detail**: A contact detail that is not meant to be shared with others

---

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more _exploratory_ testing.

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

### Deleting a person

1. Deleting a person while all persons are being shown
   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete A0123456J` (assuming a person with this student ID exists)<br>
      Expected: Contact with student ID A0123456J is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Test case: `delete A9999999W` (assuming no person with this student ID exists)<br>
      Expected: No person is deleted. Error message "Error, user does not exist." shown in the status message.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `delete INVALID_STUDENTID`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files
   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
