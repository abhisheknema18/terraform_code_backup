CREATE TABLE [dbo].[ScriptProfiles] (
    [ProfileID]          INT          NOT NULL,
    [ProfileName]        VARCHAR (30) NOT NULL,
    [ProfileDescription] VARCHAR (50) NOT NULL,
    [CreateUser]         VARCHAR (12) NOT NULL,
    [CreateDate]         INT          NOT NULL,
    [ProfileType]        VARCHAR (1)  NOT NULL
);

