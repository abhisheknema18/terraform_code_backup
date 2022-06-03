CREATE TABLE [dbo].[AccessToken]
(
	[Id] VARCHAR(40) NOT NULL, 
    [Name] VARCHAR(50) NOT NULL, 
    [LinkedUserCode] VARCHAR(10) NOT NULL, 
    [CreateUser] VARCHAR(10) NOT NULL, 
    [CreateDate] INT NOT NULL, 
    [CreateTime] VARCHAR(6) NOT NULL, 
    [TokenNotes] VARCHAR(250) NULL, 
    [ExpiryDate] INT NOT NULL, 
    [checksum] VARCHAR(128) NOT NULL, 
    [revoked] INT NOT NULL, 
    [RevokeUser] VARCHAR(10) NULL, 
    [RevokeDate] INT NULL, 
    [RevokeNotes] VARCHAR(250) NULL,
    CONSTRAINT [PK_ACCESSTOKEN] PRIMARY KEY CLUSTERED (Id ASC)
)
