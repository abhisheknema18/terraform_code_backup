CREATE TABLE [dbo].[SystemUsersAuth]
(
    [UserCode] VARCHAR(10) NOT NULL, 
    [Subject] VARCHAR(500) NOT NULL, 
    [Issuer] VARCHAR(1700) NOT NULL, 
    CONSTRAINT [PK_SystemUsersAuth] PRIMARY KEY CLUSTERED ([UserCode] ASC)
)

GO

CREATE UNIQUE NONCLUSTERED INDEX [UI_SystemUsersAuth_IDX1] 
    ON [dbo].[SystemUsersAuth] ([Subject] ASC, [Issuer] ASC)

GO