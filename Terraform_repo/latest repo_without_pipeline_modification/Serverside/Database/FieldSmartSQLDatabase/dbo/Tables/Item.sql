CREATE TABLE [dbo].[Item] (
    [ScriptID]       INT           NOT NULL,
    [SequenceNumber] INT           NOT NULL,
    [AlternateRef]   VARCHAR (200) NULL,
    [ItemText]       VARCHAR (255) NULL,
    [ItemType]       VARCHAR (25)  NULL,
    [InputType]      VARCHAR (2)   NULL,
    [ILevel]         INT           NOT NULL,
    [FieldSize]      VARCHAR (10)  NULL,
    [Precision]      INT           NULL,
    [Validation]     VARCHAR (20)  NULL,
    [DefaultVal]     VARCHAR (80)  NULL,
    [HelpID]         INT           NULL,
    [DataRef]        VARCHAR (3)   NULL,
    [DefectSetName]  VARCHAR (10)  NULL,
    [SpecialNote]    INT           NULL,
    [UOMCatID]       INT           NULL,
    [RelWeight]      INT           NULL,
    CONSTRAINT [PK_ITEM] PRIMARY KEY CLUSTERED ([ScriptID] ASC, [SequenceNumber] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_ITEM_HELPID]
    ON [dbo].[Item]([HelpID] ASC);


GO
CREATE NONCLUSTERED INDEX [I_ITEM_UOMCATID]
    ON [dbo].[Item]([UOMCatID] ASC);


GO
CREATE NONCLUSTERED INDEX [I_ITEM_DEFECTSETNAME]
    ON [dbo].[Item]([DefectSetName] ASC);


GO
CREATE NONCLUSTERED INDEX [I_ITEM_ITEMTYPE]
    ON [dbo].[Item]([ItemType] ASC);


GO
CREATE NONCLUSTERED INDEX [I_ITEM_VALIDATION]
    ON [dbo].[Item]([Validation] ASC);

