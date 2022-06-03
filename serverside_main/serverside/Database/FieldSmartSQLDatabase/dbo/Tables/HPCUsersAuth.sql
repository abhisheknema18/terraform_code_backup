CREATE TABLE [dbo].[HPCUsersAuth]
(
    [UserCode] VARCHAR(32) NOT NULL, 
    [Subject] VARCHAR(500) NOT NULL, 
    [Issuer] VARCHAR(1700) NOT NULL, 
    CONSTRAINT [PK_HPCUsersAuth] PRIMARY KEY CLUSTERED ([UserCode] ASC)
)

GO

CREATE UNIQUE NONCLUSTERED INDEX [UI_HPCUsersAuth_IDX1] 
    ON [dbo].[HPCUsersAuth] ([Subject] ASC, [Issuer] ASC)

GO
