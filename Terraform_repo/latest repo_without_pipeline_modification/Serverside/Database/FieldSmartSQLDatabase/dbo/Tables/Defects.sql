CREATE TABLE [dbo].[Defects] (
    [DefectCode]        VARCHAR (4)  NOT NULL,
    [DefectDescription] VARCHAR (40) NOT NULL,
    CONSTRAINT [PK_DEFECTS] PRIMARY KEY CLUSTERED ([DefectCode] ASC)
);

