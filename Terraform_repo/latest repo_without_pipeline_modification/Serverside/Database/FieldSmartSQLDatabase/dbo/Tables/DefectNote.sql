CREATE TABLE [dbo].[DefectNote] (
    [DefectCode] VARCHAR (4) NOT NULL,
    [SNID]       INT         NOT NULL,
    CONSTRAINT [PK_DEFECTNOTE] PRIMARY KEY CLUSTERED ([DefectCode] ASC, [SNID] ASC)
);

