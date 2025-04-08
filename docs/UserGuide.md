---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TutorConnect User Guide

TutorConnect is a **CLI-based student management tool designed for tutors handling multiple courses across different platforms.**
It provides a centralized solution for organizing student contacts and managing tasks—without the complexity of a full-fledged learning management system.
If you can type fast, TutorConnect can get your contact management tasks done faster than traditional GUI apps.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2425S2-CS2103-F08-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your TutorConnect.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar tutorconnect.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** will display a help message.<br>
   Some example commands you can try:

    * `todo list` : Lists all todos.

    * `event add --name CS2040S tutorial --start 24-08-26 12:00 --end 24-08-26 14:00 --location NUS SoC COM1 --tag algorithms` : Adds an event to the app.

    * `contact delete 3` : Deletes the 3rd contact shown in the current list.

    * `contact clear` : Deletes all contacts.

    * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Commands working with item will be of format `ITEM COMMAND PARAMETERS`.

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add --name NAME`, `NAME` is a parameter which can be used as `add --name John Doe`.

* Parameters in square brackets are optional.<br>
  e.g. `--name NAME [--tag TAG(S)]` can be used as `--name John Doe --tag friend`, `--name John Doe`, or `--name John Doe --tag enemy must-be-destroyed`.

* Parameters with optional plural markers can be specified a positive integer number of times.
  Note that this rule can be combined with the previous one to represent a parameter that can be specified a non-negative integer number of times.<br>
  e.g. `--contact CONTACT_INDEX/INDICES` can be used as `--contact 123` or `--contact 4 5 6`.

* Parameters can be in any order.<br>
  e.g. if the command specifies `--name NAME --location LOCATION`, `--location LOCATION --name NAME ` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `todo list`, `exit` and `event clear`) will be ignored.<br>
  e.g. if the command specifies `todo list 123`, it will be interpreted as `todo list`.

* Constraints for common parameters:
    * Most text-based parameters can accept multiple words as values, but the words cannot start with a `-` to avoid clashing with our prefixes. This applies to `NAME`, `ID`, `COURSE`, `GROUP`, and `LOCATION`.
      e.g. `NUS Science`, `E.T. the Extra-Terrestrial`, `#?!@-*#$` are acceptable, but `1 - 1 = 2` is not.
    * Datetime-based parameters must be of format `YY-MM-DD HH:MM`, with exactly one space between `DD` and `HH`, and where `HH` is in the 24-hour format.
    * `TAG` parameters must be a single word not starting with `-`.
    * Index-based parameters must be a positive integer not larger than the size of the list of interest.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
  </box>

## Common `item` Commands:
This section contains the common commands shared between `contact`, `event` and `todo`. It serves as a unified section, and it aims reduce verbosity of each `item` guide. The individual section only contains unique operations to that `item`.  

An `item` in this section is defined as one of the following `contact`, `event` and `todo`. Please replace `item` with the command of interest.
The constraints of the different flags are defined clearer at the end of all the sections. The guide below serves as an overview and example of how to use these commands.

* `contact`: To store the various contact information of a student / tutor in TutorConnect.
* `event`: To keep track of tasks that have a set duration at a location.
* `todo`: To keep track of tasks that have a deadline.

**Important**: Uniqueness for `contact` is defined as having different `ID`. Uniqueness for `event` and `todo` is defined to have different `name`

### Adding an `item`: `add`
An `item` is added to the application.

**Format & Examples:**
* `contact`:
  * Format: `contact add --id ID --name NAME --email EMAIL --course COURSE --group GROUP [--tag TAG(S)]`
  * Example: `contact add --id A1234567A --name John Doe --email johnd@example.com --course CS50 --group T01 --tag friends owesMoney`

* `event`:
  * Format: `event add --name NAME --start START_DATETIME --end END_DATETIME --location LOCATION [--tag TAG(S)]`
  * Example: `event add --name CS2040S tutorial --start 24-08-26 12:00 --end 24-08-26 14:00 --location NUS SoC COM1 --tag algorithms dataStructures`

* `todo`:
  *  `todo add --name NAME --location LOCATION --deadline DEADLINE [--tag TAG(S)]`
  * Example: `todo add --name Final Submission --deadline 24-08-26 12:00 --location NUS SoC COM1 --tag CS1234`

**Pro-tip**: The various `items` will be added to their respective lists. `contact` will be added to the contact list on the right side, `event` and `todo` will be added to the list on the bottom left side to their respective list.

### Editing an `item`: `edit`
Edits the details of the `item` identified by the index number in the currently displayed on their respective item list. Only the fields specified in the command will be updated; all others remain unchanged.

**Format & Examples**:
* `contact`
  * Format: `contact edit CONTACT_INDEX [--id ID] [--name NAME] [--email EMAIL] [--course COURSE] [--group GROUP] [--tag TAG(S)]`
  * Example: `contact edit 1 --email johndoe@example.com --tag --name John Doe` 

* `event`
  * Format: `event edit EVENT_INDEX [--name NAME] [--start START_TIME] [--end END_TIME] [--location LOCATION] [--contact CONTACT_INDEX/INDICES] [--tag TAG(S)]` 
  * Example: `event edit 2 --start 25-03-15 10:00 --end 25-03-15 12:00`
* `todo`
  * Format: `todo edit TODO_INDEX [--name NAME] [--deadline DEADLINE] [--location LOCATION] [--status STATUS] [--contact CONTACT_INDEX/INDICES] [--tag TAG(S)]`
  * Example: `todo edit 2 --deadline 25-03-15 10:00 --location COM2-0208 --tag --status false`

**Important**: `ITEM_INDEX` is the index present in the UI, `CONTACT_INDEX` represents the index of the item on the item on the contact list on right. `EVENT/TODO_INDEX` is the index of the `event` or `todo` of the selected list on the bottom left.

**Pro Tip**: At least one of the flags must be present to be edited.

**Pro-er Tip**: If `--tag` is empty, all the tags for the `item` will be cleared. Not the rest of the data fields though...

### Deleting an `item`: `delete`
Deletes an item from its corresponding list. Removing it from *existence*. 

**Format & Examples**
* `item`
  * Format: `item delete ITEM_INDEX`
  * Example: 
    * `contact delete 1`
    * `event delete 2`
    * `todo delete 3`

### Getting information: `info` 
Displays the full information of the `item` of interest as requested by the user.

**Format & Examples**
* Format: `item info ITEM_INDEX`
  * Example: 
    * `contact info 1`
    * `event info 2`
    * `todo info 3`

**Pro Tip**: You can click on the `item` card to view the item of interest. This operation is equivalent to using the `info` command.

### Getting full `item` list: `list`
Displaying the full `item` list. Useful for displaying the full `item` list after `filter` operation.
Operator between columns is `and`, and the operator specified is **ONLY** within columns

The `filter` stays there even after you execute other commands. Use another `filter` or list to overwrite the `filter`.

**Format & Examples**
* Format: `item list`
  * Example: 
    * `contact list`
    * `event list`
    * `todo list`

**Pro-tip & Important**: The refresh button on the side is **NOT A UNDO BUTTON**, clicking the button here `list` the current list that is being viewed.

### Clearing `item` list: `clear`
Clears the entire list of the chosen `item` list.

**Format & Examples**
* Format: `item clear`
  * Example: 
    * `contact clear`
    * `event clear`
    * `todo clear`

**WARNING**: The cleared `item` list *cannot* be undone, it is **NUKING** the `item` list.

### Filtering an `item`: `filter`
Filters the list of `item` based on one or more criteria. You can search by each `item`'s specified `--column` or linked contacts using logical operators to combine multiple values.

Format: `item filter --COLUMN [OPERATOR:] VALUE(S) [...]`

Supported `contact` columns
* `--name`. Filter by `contact` name.
* `--email`. Filter by `contact` email.
* `--id`. Filter by `contact` ID.
* `--course`. Filter by `contact` course.
* `--group`. Filter by `contact` group.
* `--tag`. Filter by `contact` tag.

Supported `event` columns:
* `--name`
* `--start`. To filter the event start time, within the duration of`[<INTERVAL_START>/<INTERVAL_END>]`.
* `--end`. To filter the event end time, within the duration of `[<INTERVAL_START>/<INTERVAL_END>]`.
* `--location`.
* `--tag`
* `--contact`.

Supported `todo` columns:
* `--name`.
* `--location`.
* `--deadline`. To filter todo deadlines, within the duration of `[<INTERVAL_START>/<INTERVAL_END>]`.
* `--status`. Note that operators are not allowed for this flag.
* `--tag`.
* `--contact`.

`Operators` (optional):
* `and` (default): All values must match.
* `or`: At least one value must match.
* `nand`: Not all the values must match.
* `nor`: None of the values must match.

**Pro-tip**: Please refer to the table of constraints for a more concrete definition of the flags being used in `filter`.

If an operator is not provided, it defaults to `and`. If an unrecognized operator is provided, it will be treated as a value. If multiple valid operators are provided, the first one will be applied and the rest will be treated as values.

`filter` finds all contacts which fulfil all the specified criteria. Specify at least one criterion. Operators can only be placed within a specific column.

`--tag`: A keyword matching an `item`'s tags means the keyword is contained in at least one of the `item`'s tags.

Provide one or more keywords separated by spaces.
Keywords are case-insensitive and support partial matches.

Examples:
* `contact filter --id or: 12 13`.
  * Find students with ID 12 or 13.

* `contact filter --name nand: enemy Hater --tag and: handsome smart`.
  * Find contacts whose names do not contain both "enemy" and "Hater" and are tagged with both "handsome" and "smart".

* `event filter --name or: Exam PRESENTATION`.
  * Find events whose name contains at least one of the keywords "exam" or "presentation."

* `event filter --name CS1010S eXAM --start or: [25-03-13 23:59/25-03-20 23:59] [25-03-27 23:59/-]`
    * Find events whose name contains both the keywords "`CS1010S`" and "`exam`" and whose start time is between `25-03-13 23:59` and `25-03-20 23:59`(inclusive) or after `25-03-27 23:59` (inclusive).

* `todo filter --name or: Exam REPORT`.
  * Find todos whose name contains at least one of the keywords `"exam"` or `"report"`.

* `todo filter --name CS1010S grading --deadline or: [25-04-13 23:59/25-04-20 23:59] [25-04-27 23:59/-]`
  * Find todos whose name contains both the keywords "`CS1010S`" and "`grading`" and whose start time is between `25-04-13 23:59` and `25-04-20 23:59`(inclusive) or after `25-04-27 23:59` (inclusive).

**Tip**: Do not get overwhelmed! You can simply use filter as a simple search tool. Logical operations in the examples are for specialized queries so do try and play around with the combinations.

**Fun fact**: For filtering on intervals. While you are allowed to put the start of the interval after the end of the interval, this may (*obviously!*) result in unhelpful results.

### Tagging items: `tag`
Adding `tag` to a specified `item`.
**Format & Examples**
* Format: `item tag ITEM_INDEX --tag TAG(S)`
  * Example: 
    * `contact tag 1 --tag friend`
    * `event tag 2 --tag presenting guest vip`
    * `todo tag 4 --tag grading`

**Important**: At least one `tag` must be provided.

**Pro-tip**: You can add multiple tags for any `item` that you are interested in.

### Untagging items: `untag`
Removing specified `tag` at the provided `item`'s `index`.

**Format & Examples**
* Format: `item untag ITEM_INDEX --tag TAG(s)`
  * Example: 
    * `contact untag 1 --tag friend`
    * `event untag 2 --tag presenting guest vip`
    * `todo untag 4 --tag grading`

## Event: `event`
A `event` is a task with a duration that can be associated with multiple contacts to indicate their involvement / attendance. The `event` supports the following unique operations.

### Linking contacts: `link`
Associates one or more contacts to an event. Useful for keeping track of which contacts are involved in a particular event.

Format: `event link EVENT_INDEX --contact CONTACT_INDEX/INDICES`

* `EVENT_INDEX` refers to the index of the event in the displayed event list on the bottom left.

* `CONTACT_INDEX/INDICES` refers to the index(es) of the contact(s) in the currently displayed contact list.

* You can link multiple contacts by providing more than one contact index.

* At least one `CONTACT_INDEX` must be provided.

Examples:
* `event link 1 --contact 1 3 4`

### Unlinking contacts: `unlink`
Removes the association between one or more contacts and a specific event.

Format: `event unlink EVENT_INDEX --contact CONTACT_INDEX/INDICES`

* `EVENT_INDEX` refers to the index of the event in the displayed event list.

* `CONTACT_INDEX/INDICES` refers to the index(es) of the contact(s) to be unlinked from the event.

* You can unlink multiple contacts by providing more than one contact index.

Examples:
* `event unlink 1 --contact 3 4`
* `event untag 1 --tag important weekly`

### Logging attendance for an event: `log`
Records the attendance of one or more contacts for a specific event. This allows you to track who actually attended, separate from just being linked to the event.

Format: `event log EVENT_INDEX --contact CONTACT_INDEX [CONTACT_INDEX/INDICES]`

* `EVENT_INDEX` refers to the index of the event in the displayed event list.

* `CONTACT_INDEX/INDICES` refers to the index(es) of the contacts linked to the event, as shown by event info.

* You can log multiple contacts at once by listing their indices.

* You cannot unlog a contact that is already unlogged.

**Tip:** Use event info to view the current linked contacts and their indices.

Example:
* `event log 1 --contact 1 2 3`
  (Logs attendance for contacts 1, 2, and 3 at the event with index 1)

### Removing attendance logs from an event: `unlog`
Removes the attendance logs of one or more contacts from a specific event. Useful for correcting mistakes or updating attendance records.

Format:
`event unlog EVENT_INDEX --contact CONTACT_INDEX [CONTACT_INDEX/INCIDES]`

* `EVENT_INDEX` refers to the index of the event in the displayed event list.

* `CONTACT_INDEX/INDICES` refers to the index(es) of the contacts previously logged for that event, as shown in event info.

* You can unlog multiple contacts at once by listing their indices.

* You cannot unlog a contact that is already unlogged

**Tip:** You can click on the event to display the full information.

Example:
`event unlog 1 --contact 1 2 3`
(Removes attendance logs for contacts 1, 2, and 3 from the event with index 1)

## Todo: `todo`
A todo is a task with a deadline that can be associated with multiple contacts to indicate their involvement. It supports the following commands.

### Linking contacts: `link`
Associates one or more contacts to a todo. Useful for keeping track of which contacts are involved in a particular todo.

Format: `todo link TODO_INDEX --contact CONTACT_INDEX/INDICES`

* `TODO_INDEX` refers to the todo of the event in the displayed todo list.

* `CONTACT_INDEX/INDICES` refers to the index(es) of the contact(s) in the currently displayed contact list. You can link multiple contacts by providing more than one contact index.

Examples:
* `todo link 1 --contact 1 3 4`

### Mark a todo as done: `mark`
Mark a todo as completed.

Format: `todo mark TODO_INDEX`

* `TODO_INDEX` refers to the index of the todo in the displayed todo list.

* The todo must not be already marked as done.

Example:
* `todo mark 1`

### Mark a todo as not done: `unmark`
Mark a todo as not done. This reverses the effect of the `mark` command.

Format: `todo unmark TODO_INDEX`

* `TODO_INDEX` refers to the index of the todo in the displayed todo list.

* The todo must not be already marked as not done.

Example:
`todo unmark 20`

## Table of Constraints (Flags)
|Parameter|Constraint|item/command|Example|
|:---|:---|:---|:--|
|`--name`|Can contain multiple words having special characters, but the words cannot start with `-`. Must not be blank.|`contact`, `event`, `todo`|+ `--name David Li `<br>- `--name -Alvin`|
| `--email`  | Must follow `local-part@domain` format.<br><br>**Local part:**<br>- Alphanumeric + allowed: `+`, `_`, `.`, `-`<br>- Cannot start/end with special characters<br><br>**Domain:**<br>- Labels separated by `.`<br>- Ends with label ≥ 2 chars<br>- Each label starts/ends with alphanumeric<br>- Hyphens allowed *within* labels only |`contact`|+ `--email user.name+filter@example-domain.com`<br>- `--email -username@domain.com`<br>- `--email user@.com`|
|`--id`|Can contain multiple words having special characters, but the words cannot start with `-`. Must not be blank.|`contact`|+ `--id id`<br> - `--id -id`|
|`--course`|Can contain multiple words having special characters, but the words cannot start with `-`. Must not be blank.|`contact`|+ `--course course`<BR> - `--course -course`|
|`--group`|Can contain multiple words having special characters, but the words cannot start with `-`. Must not be blank.|`contact`|+ `--group T01 01`<BR> - `--group -group`|
|`--tag`|A word that cannot start with `-`. Must not be blank|`contact`, `event`, `todo`|+ `--tag tag`<BR>- `--tag -tag`<BR>- `--tag tag t` (tags `t` and `tag` will be added)|
|`--start`|`yy-mm-dd hh:mm`, `hh` is of 24 hours format.|`event`|+ `--start 24-08-26 12:00` <BR> - `--start 24/08/26 1400`|
|`--end`|`yy-mm-dd hh:mm`, `hh` is of 24 hours format.|`event`|+ `--end 24-08-26 12:00` <BR> - `--end 24/08/26 1400`|
|`--deadline`|`yy-mm-dd hh:mm`, `hh` is of 24 hours format.|`todo`|+ `--deadline 24-08-26 12:00` <BR> - `--deadline 24/08/26 1400`|
|`--contact`|Positive integer, from 1 to the size of the `contact` list.|`event`, `todo`|+ `--contact 1` <BR> - `--contact abc`|


**Important**:
* `--start`, `--end`, `--deadline` format must contain exactly one space between `dd` and `hh`.
* `--contact` for `event` and `todo` is from the information from `list`.

## Miscellaneous

### Listing all subcommands of a feature: `help`

Lists all subcommands of a feature.

Format: `help FEATURE`

Examples:
* `help contact` shows all subcommands of contact feature.
* `help todo` shows all subcommands of todo feature.
* `help event` shows all subcommands of event feature.

### Listing help message of a subcommand of a feature: `help`

Lists help message of a subcommand of a feature.

Format: `help FEATURE SUBCOMMAND`

Examples:
* `help contact add` shows help message of `contact add` command.
* `help todo mark` shows help message of `todo mark` command.
* `help event log` show help message of `event log` command.

### Exiting the program : `exit`/`quit`/`kill`/`bye`

## Quality of Life:

1. You may use the arrows to traverse through command history.

1. Clicking on each contact/event/todo card will display the full information of the card in the command output.

1. Click on "Event" or "Todo" button to toggle between event and todo list views. You may use the reset button to reset filtered views of event/todo and contacts at the same time.

1. Apply natural ordering to lists:
  * Contacts should be sorted first by name, then by ID in alphabetical order.
  * Todos should be sorted by status first, followed by deadline, and then by name.
  * Events should be sorted by start time, then by end time, and finally by name.

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TutorConnect home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------
