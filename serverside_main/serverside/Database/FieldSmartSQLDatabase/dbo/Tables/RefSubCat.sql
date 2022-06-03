CREATE TABLE [dbo].[RefSubCat] (
    [RefSubCatId]   INT          NOT NULL,
    [RefSubCatType] VARCHAR (2)  NULL,
    [RefSubCatDesc] VARCHAR (40) NULL,
    [RefCatId]      INT          NULL,
    CONSTRAINT [PK_REFSUBCAT] PRIMARY KEY CLUSTERED ([RefSubCatId] ASC)
);

