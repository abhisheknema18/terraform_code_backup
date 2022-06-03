CREATE TABLE [dbo].[RefItem] (
    [RefId]       INT           NOT NULL,
    [RefDesc]     VARCHAR (100) NULL,
    [RefSubCatId] INT           NULL,
    [OtherRef]    VARCHAR (100) NULL,
    CONSTRAINT [PK_REFITEM] PRIMARY KEY CLUSTERED ([RefId] ASC)
);

