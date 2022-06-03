CREATE TABLE [dbo].[ValFreeTextReq] (
    [ScriptID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [ValidationType] VARCHAR (15)  NOT NULL,
    [Response]       VARCHAR (80)  NOT NULL,
    [DisplayMessage] VARCHAR (100) NULL,
    CONSTRAINT [PK_VALFREETEXTREQ] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC, [ValidationType] ASC, [Response] ASC)
);


GO
CREATE NONCLUSTERED INDEX [VALFREETXTREQ_SID]
    ON [dbo].[ValFreeTextReq]([ScriptID] ASC);

