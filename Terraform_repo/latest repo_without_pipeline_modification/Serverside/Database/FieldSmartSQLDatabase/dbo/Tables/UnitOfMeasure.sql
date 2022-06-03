CREATE TABLE [dbo].[UnitOfMeasure] (
    [UOMCatID] INT          NOT NULL,
    [UOMType]  VARCHAR (12) NOT NULL,
    [Pref]     INT          NULL,
    CONSTRAINT [PK_UNITOFMEASURE] PRIMARY KEY CLUSTERED ([UOMCatID] ASC, [UOMType] ASC)
);

