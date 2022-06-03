CREATE TABLE [dbo].[ResultNotes] (
    [ReturnID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [ResorderNo]     INT           NOT NULL,
    [NoteText]       VARCHAR (150) NULL,
    [CreateUser]     VARCHAR (10)  NULL,
    [CreateDate]     INT           NULL,
    [CreateTime]     INT           NULL
);


GO
CREATE NONCLUSTERED INDEX [I_RESULTNOTES_RESORDERNO]
    ON [dbo].[ResultNotes]([ResorderNo] ASC);


GO
CREATE NONCLUSTERED INDEX [I_RESULTNOTES_RTNID]
    ON [dbo].[ResultNotes]([ReturnID] ASC);

