CREATE TABLE [dbo].[ResultFiles] (
    [FileName]   VARCHAR (255) NOT NULL,
    [ResultSet]  IMAGE         NULL,
    [Commitdate] INT           NULL,
    [CommitTime] INT           NULL,
    CONSTRAINT [PK_RESULTFILES] PRIMARY KEY CLUSTERED ([FileName] ASC)
);

