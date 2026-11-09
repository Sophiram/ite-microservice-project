@import java.util.Map
@import kh.edu.istad.iam.application.result.UserResult
@param Map<String, String> messages
@param String localeTag
@param String csrfParameterName
@param String csrfToken
@param UserResult user
@param boolean saved
<!doctype html>
<html lang="${localeTag}"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body><header><a href="/">${messages.get("common.home")}</a></header><main><section class="card"><h1>${messages.get("profile.title")}</h1>
@if (saved)<p class="notice">${messages.get("common.success")}</p>@endif
<form action="/profile/edit" method="post"><input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
<div class="grid">
<div><label>${messages.get("profile.givenName")}</label><input name="givenName" value="${user.profile().givenName() == null ? "" : user.profile().givenName()}"></div>
<div><label>${messages.get("profile.familyName")}</label><input name="familyName" value="${user.profile().familyName() == null ? "" : user.profile().familyName()}"></div>
<div><label>${messages.get("profile.middleName")}</label><input name="middleName" value="${user.profile().middleName() == null ? "" : user.profile().middleName()}"></div>
<div><label>${messages.get("profile.nickname")}</label><input name="nickname" value="${user.profile().nickname() == null ? "" : user.profile().nickname()}"></div>
<div><label>${messages.get("profile.gender")}</label><input name="gender" value="${user.profile().gender() == null ? "" : user.profile().gender()}"></div>
<div><label>${messages.get("profile.birthdate")}</label><input name="birthdate" type="date" value="${user.profile().birthdate() == null ? "" : user.profile().birthdate().toString()}"></div>
<div><label>${messages.get("profile.phoneNumber")}</label><input name="phoneNumber" value="${user.profile().phoneNumber() == null ? "" : user.profile().phoneNumber()}"></div>
<div><label>${messages.get("profile.locale")}</label><select name="locale"><option value="en">${messages.get("common.english")}</option><option value="km">${messages.get("common.khmer")}</option></select></div>
<div><label>${messages.get("profile.profileUrl")}</label><input name="profileUrl" value="${user.profile().profileUrl() == null ? "" : user.profile().profileUrl()}"></div>
<div><label>${messages.get("profile.pictureUrl")}</label><input name="pictureUrl" value="${user.profile().pictureUrl() == null ? "" : user.profile().pictureUrl()}"></div>
<div><label>${messages.get("profile.websiteUrl")}</label><input name="websiteUrl" value="${user.profile().websiteUrl() == null ? "" : user.profile().websiteUrl()}"></div>
<div><label>${messages.get("profile.zoneinfo")}</label><input name="zoneinfo" value="${user.profile().zoneinfo() == null ? "" : user.profile().zoneinfo()}"></div>
<div><label>${messages.get("profile.streetAddress")}</label><input name="streetAddress" value="${user.address().streetAddress() == null ? "" : user.address().streetAddress()}"></div>
<div><label>${messages.get("profile.locality")}</label><input name="locality" value="${user.address().locality() == null ? "" : user.address().locality()}"></div>
<div><label>${messages.get("profile.region")}</label><input name="region" value="${user.address().region() == null ? "" : user.address().region()}"></div>
<div><label>${messages.get("profile.postalCode")}</label><input name="postalCode" value="${user.address().postalCode() == null ? "" : user.address().postalCode()}"></div>
<div><label>${messages.get("profile.country")}</label><input name="country" maxlength="2" value="${user.address().country() == null ? "" : user.address().country()}"></div>
</div><p><button>${messages.get("profile.save")}</button></p></form></section></main></body></html>
