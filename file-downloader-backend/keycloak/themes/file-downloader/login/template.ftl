<#macro registrationLayout displayInfo=false displayWide=false displayMessage=false>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="robots" content="noindex, nofollow">

  <title><#nested "title"> - File Downloader</title>

  <#if properties.styles?has_content>
    <#list properties.styles?split(' ') as style>
      <link href="${url.resourcesPath}/${style}" rel="stylesheet">
    </#list>
  </#if>
  <#if properties.scripts?has_content>
    <#list properties.scripts?split(' ') as script>
      <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
    </#list>
  </#if>
  <#if scripts??>
    <#list scripts as script>
      <script src="${script}" type="text/javascript"></script>
    </#list>
  </#if>
</head>
<body>
  <div id="kc-container">
    <div id="kc-content">
      <div id="kc-content-wrapper">
        <div id="kc-page-title">
          <span class="brand">File Downloader</span>
          <span class="form-title"><#nested "header"></span>
        </div>

        <#if displayMessage && message?? && message.summary??>
          <div class="alert alert-${message.type}">
            ${message.summary}
          </div>
        </#if>

        <#if messagesPerField?? && messagesPerField.existsError()>
          <div class="alert alert-error">
            <#list messagesPerField.allErrors as error>
              ${kcSanitize(msg(error))?no_esc}<br>
            </#list>
          </div>
        </#if>

        <#nested "form">

        <#if displayInfo>
          <#nested "socialProviders">
          <#nested "info">
        </#if>
      </div>
    </div>
  </div>
</body>
</html>
</#macro>
