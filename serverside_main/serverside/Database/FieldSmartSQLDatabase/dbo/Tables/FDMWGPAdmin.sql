﻿CREATE TABLE [dbo].[FDMWGPAdmin] (
    [GroupCode] VARCHAR (10) NOT NULL,
    [WGCATId]   INT          NOT NULL,
    CONSTRAINT [PK_FDMWGPADMIN] PRIMARY KEY CLUSTERED ([GroupCode] ASC, [WGCATId] ASC)
);

