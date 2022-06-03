CREATE TABLE [dbo].[Help] (
    [HelpID]    INT           NOT NULL,
    [HelpCatID] INT           NULL,
    [HelpDesc]  VARCHAR (25)  NOT NULL,
    [HelpText]  VARCHAR (255) NOT NULL,
    CONSTRAINT [PK_HELP] PRIMARY KEY CLUSTERED ([HelpID] ASC)
);

