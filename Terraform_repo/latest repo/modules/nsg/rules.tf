variable "rules" {
  type = map(any)
  default = {
    ElasticSearch = ["Inbound", "Allow", "TCP", "*", "9200-9300", "ElasticSearch"]
    HTTP          = ["Inbound", "Allow", "TCP", "*", "80", "HTTP"]
    HTTPS         = ["Inbound", "Allow", "TCP", "*", "443", "HTTPS"]
    IMAP          = ["Inbound", "Allow", "TCP", "*", "143", "IMAP"]
    IMAPS         = ["Inbound", "Allow", "TCP", "*", "993", "IMAPS"]
    MSSQL         = ["Inbound", "Allow", "TCP", "*", "1433", "MSSQL"]
    RDP           = ["Inbound", "Allow", "TCP", "*", "3389", "RDP"]
    SMTP          = ["Inbound", "Allow", "TCP", "*", "25", "SMTP"]
    SMTPS         = ["Inbound", "Allow", "TCP", "*", "465", "SMTPS"]
    SSH           = ["Inbound", "Allow", "TCP", "*", "22", "SSH"]
    WinRM         = ["Inbound", "Allow", "TCP", "*", "5986", "WinRM"]
  }
}
