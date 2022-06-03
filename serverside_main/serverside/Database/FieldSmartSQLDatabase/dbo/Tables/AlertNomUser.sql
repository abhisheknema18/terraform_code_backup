CREATE TABLE [dbo].[AlertNomUser] (
    [HPCWorkGroup] VARCHAR (10) NOT NULL,
    [UserCode]     VARCHAR (10) NOT NULL,
    [Email]        VARCHAR (80) NULL,
    CONSTRAINT [PK_ALERTNOMUSER] PRIMARY KEY CLUSTERED ([HPCWorkGroup] ASC, [UserCode] ASC)
);

