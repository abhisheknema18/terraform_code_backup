CREATE TABLE [dbo].[EventTypeFieldRoles] (
    [EventID]     INT NOT NULL,
    [FieldRoleID] INT NOT NULL,
    CONSTRAINT [PK_EventTypeFieldRoles] PRIMARY KEY CLUSTERED ([EventID] ASC, [FieldRoleID] ASC)
);

