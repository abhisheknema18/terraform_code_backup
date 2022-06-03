CREATE TABLE [dbo].[ScriptResults] (
    [ReturnID]       INT           NOT NULL,
    [ResOrderNo]     INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [ResponseType]   VARCHAR (3)   NULL,
    [Response]       VARCHAR (100) NULL,
    [UOM]            VARCHAR (12)  NULL,
    [PreValue]       VARCHAR (100) NULL,
    [OotFlag]        INT           NULL,
    [ResultDate]     INT           NULL,
    [ResultTime]     INT           NULL,
    [Status]         VARCHAR (5)   NULL,
    [Score]          INT           NULL,
    [MaxScore]       INT           NULL,
    [WeightScore]    INT           NULL,
    CONSTRAINT [PK_SCRIPTRESULTS] PRIMARY KEY CLUSTERED ([ReturnID] ASC, [ResOrderNo] ASC, [SequenceNumber] ASC)
);

