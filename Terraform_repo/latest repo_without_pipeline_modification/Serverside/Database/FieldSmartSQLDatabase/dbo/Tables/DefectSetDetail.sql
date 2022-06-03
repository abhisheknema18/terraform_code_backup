CREATE TABLE [dbo].[DefectSetDetail] (
    [DefectSetName] VARCHAR (10) NOT NULL,
    [DefectCode]    VARCHAR (4)  NOT NULL,
    CONSTRAINT [PK_DEFECTSETDETAIL] PRIMARY KEY CLUSTERED ([DefectSetName] ASC, [DefectCode] ASC)
);

