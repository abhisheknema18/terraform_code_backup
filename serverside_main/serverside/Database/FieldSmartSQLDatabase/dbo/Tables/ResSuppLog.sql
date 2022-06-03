CREATE TABLE [dbo].[ResSuppLog] (
    [ReturnID]  INT           NOT NULL,
    [EditRefNo] INT           NOT NULL,
    [Response]  VARCHAR (100) NOT NULL,
    CONSTRAINT [PK_RESSUPLOG] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [EditRefNo] ASC, [Response] ASC)
);

