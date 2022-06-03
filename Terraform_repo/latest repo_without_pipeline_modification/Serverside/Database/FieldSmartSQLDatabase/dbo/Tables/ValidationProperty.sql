CREATE TABLE [dbo].[ValidationProperty] (
    [ValidationType]     VARCHAR (15) NOT NULL,
    [ValidationProperty] VARCHAR (80) NOT NULL,
    [EquivValue]         VARCHAR (80) NULL,
    [WeightScore]        INT          NULL,
    [Colour]             VARCHAR (12) NULL,
    CONSTRAINT [PK_VALIDATIONPROPERTY] PRIMARY KEY CLUSTERED ([ValidationType] ASC, [ValidationProperty] ASC)
);

