CREATE TABLE [dbo].[SystemWorkStatus] (
    [StatusCategory]    VARCHAR (20) NOT NULL,
    [StatusDesignation] VARCHAR (30) NOT NULL,
    [Status]            VARCHAR (20) NOT NULL,
    CONSTRAINT [PK_SystemWorkStatus] PRIMARY KEY CLUSTERED ([Status] ASC)
);

