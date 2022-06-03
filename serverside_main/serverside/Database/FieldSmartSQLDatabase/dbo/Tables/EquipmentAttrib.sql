CREATE TABLE [dbo].[EquipmentAttrib] (
    [EquipNo]       VARCHAR (30)  NOT NULL,
    [AttributeName] VARCHAR (80)  NOT NULL,
    [Value]         VARCHAR (255) NOT NULL,
    CONSTRAINT [PK_EquipmentAttrib] PRIMARY KEY CLUSTERED ([EquipNo] ASC, [AttributeName] ASC)
);

