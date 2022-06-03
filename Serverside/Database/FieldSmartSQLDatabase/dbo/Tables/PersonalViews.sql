CREATE TABLE [dbo].[PersonalViews] (
    [ViewID]         INT          NOT NULL,
    [ViewName]       VARCHAR (30) NOT NULL,
    [ViewDesc]       VARCHAR (50) NOT NULL,
    [CreateUser]     VARCHAR (12) NOT NULL,
    [Createdate]     INT          NOT NULL,
    [ModifyUser]     VARCHAR (12) NULL,
    [ModifyDate]     INT          NULL,
    [SearchMenu]     INT          NULL,
    [MostRecentOnly] INT          NULL,
    [LogicalOp]      VARCHAR (3)  NULL,
    CONSTRAINT [PK_PERSONALVIEWS] PRIMARY KEY CLUSTERED ([ViewID] ASC)
);

