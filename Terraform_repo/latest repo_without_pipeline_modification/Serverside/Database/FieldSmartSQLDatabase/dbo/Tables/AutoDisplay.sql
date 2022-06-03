CREATE TABLE [dbo].[AutoDisplay] (
    [ValidationType]     VARCHAR (15) NOT NULL,
    [ValidationProperty] VARCHAR (80) NOT NULL,
    CONSTRAINT [PK_AUTODISPLAY] PRIMARY KEY CLUSTERED ([ValidationType] ASC, [ValidationProperty] ASC)
);

