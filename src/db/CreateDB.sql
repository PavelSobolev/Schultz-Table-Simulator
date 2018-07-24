CREATE TABLE Accounts (
    ID   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    Name VARCHAR (25) UNIQUE NOT NULL,
    CreationDate DATE NOT NULL,
    ExitDate DATE NOT NULL,
    ExitTime TIME NOT NULL
);

CREATE TABLE Drills (
    ID  INTEGER PRIMARY KEY AUTOINCREMENT,
    AccID INTEGER REFERENCES accounts (ID) NOT NULL,
    DriilDate DATE NOT NULL,
    DrillTime TIME NOT NULL
);

CREATE TABLE Results (
    ResID INTEGER PRIMARY KEY AUTOINCREMENT,
    DrillID INTEGER NOT NULL REFERENCES Drills (ID),
    SearchDuration INTEGER NOT NULL CHECK (SearchDuration >= 0),
    SeqNumber INTEGER NOT NULL
);

CREATE TABLE AccountPref (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    AccID INTEGER REFERENCES accounts (ID) NOT NULL,
    meshCount INTEGER NOT NULL DEFAULT (5),
    meshSize INTEGER NOT NULL DEFAULT (70),
    symbolSource VARCHAR (12) NOT NULL DEFAULT ('Numbers') 
        CHECK (symbolSource = 'Consecutive integers' OR symbolSource = 'Hierogliphs' OR symbolSource = 'Random set of letters and signs'),
    rotate INTEGER NOT NULL DEFAULT (0) CHECK (rotate = 0 OR rotate = 1),
    afterClick   VARCHAR (13) NOT NULL
        CHECK (afterClick = 'Do nothing' OR afterClick = 'Hide the cell' OR afterClick = 'Pale the cell' OR afterClick = 'Mix symbols in the table')
        DEFAULT ('Pale the cell') 
);