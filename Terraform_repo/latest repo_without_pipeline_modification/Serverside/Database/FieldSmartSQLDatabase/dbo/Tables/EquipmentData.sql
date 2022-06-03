CREATE TABLE [dbo].[EquipmentData] (
    [EquipNo]       VARCHAR (30)  NOT NULL,
    [ParentEquipNo] VARCHAR (30)  NULL,
    [AltEquipRef]   VARCHAR (30)  NULL,
    [EquipDesc]     VARCHAR (80)  NULL,
    [EquipType]     VARCHAR (80)  NULL,
    [Latitude]      VARCHAR (255) NULL,
    [Longitude]     VARCHAR (255) NULL,
    [AttribDate]    INT           NULL,
    CONSTRAINT [PK_EquipmentData] PRIMARY KEY CLUSTERED ([EquipNo] ASC)
);


GO
CREATE NONCLUSTERED INDEX [I_EQDATA_ALTEQUIPREF]
    ON [dbo].[EquipmentData]([AltEquipRef] ASC);


GO
CREATE NONCLUSTERED INDEX [I_EQDATA_PARENTEQUIPNO]
    ON [dbo].[EquipmentData]([ParentEquipNo] ASC);

