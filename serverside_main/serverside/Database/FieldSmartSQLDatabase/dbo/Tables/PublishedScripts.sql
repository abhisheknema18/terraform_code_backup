CREATE TABLE [dbo].[PublishedScripts] (
    [ScriptID]    		INT           NOT NULL,
    [PublishDate] 		INT           NOT NULL,
    [PublishTime] 		VARCHAR (6)   NOT NULL,
    [PublishUser] 		VARCHAR (10)  NOT NULL,
    [ScriptFile]  		IMAGE         NOT NULL,
    [ScriptFileName]	VARCHAR (100) NOT NULL,
    [CheckSum] 			VARCHAR (32)  NOT NULL,
    CONSTRAINT [PK_PUBLISHEDSCRIPTS] PRIMARY KEY CLUSTERED ([ScriptId] ASC)
);