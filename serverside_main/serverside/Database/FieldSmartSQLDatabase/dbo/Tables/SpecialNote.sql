CREATE TABLE [dbo].[SpecialNote] (
    [SNID]      INT           NOT NULL,
    [SpCatId]   INT           NOT NULL,
    [NoteDesc]  VARCHAR (50)  NOT NULL,
    [NoteText]  VARCHAR (255) NOT NULL,
    [AutoPopUp] INT           NULL,
    CONSTRAINT [PK_SPECIALNOTE] PRIMARY KEY CLUSTERED ([SNID] ASC)
);

