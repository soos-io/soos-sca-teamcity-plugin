<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    const copyToClipboard = url => {
        if (navigator && navigator.clipboard && navigator.clipboard.writeText) {
            return navigator.clipboard.writeText(url);
        } else {
            alert('The Clipboard API is not available, please copy the URL manually from here: ', url)
            return Promise.reject('The Clipboard API is not available.');
        }
    }
</script>

<c:if test="${isSuccessful}">
    <div style="margin: auto;
                max-width: 50%;
                text-align: center;">

    <a href="http://soos.io"><img class="max-width" style="display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:100% !important; width:100%; height:auto !important;" width="600" alt="SOOS" data-proportionally-constrained="true" data-responsive="true" src="http://cdn.mcauto-images-production.sendgrid.net/ad9c1fd7de1b8c13/6da70ead-21f6-4c6e-b6fa-572fb3fa647e/1560x415.png"></a>
    <!-- Blue line -->
    <div style="margin:20px 0; padding:0 0 6px 0; background-color: #001369"></div>

    <c:if test="${url.contains('status')}">
        <button style="cursor: pointer; background-color:#FF486A; border:1px solid #FF486A; border-color:#FF486A; border-radius:6px; border-width:1px; color:#ffffff; display:inline-block; font-size:14px; font-weight:normal; letter-spacing:0px; line-height:normal; padding:12px 18px 12px 18px; text-align:center; text-decoration:none; border-style:solid;" onclick="copyToClipboard('${url}')">Click here to copy the Report Status URL</button>
        <p style="font-size:14px; font-weight:normal; letter-spacing:0; line-height:normal; padding:12px 18px 12px 18px; text-align:center; color: #001369">
            <strong>NOTE:</strong> You will need this copied URL when you select the <strong>async result</strong> mode.
        </p>
    </c:if>
    <c:if test="${not url.contains('status')}">
        <a href="${url}" style="background-color:#FF486A; border:1px solid #FF486A; border-color:#FF486A; border-radius:6px; border-width:1px; color:#ffffff; display:inline-block; font-size:14px; font-weight:normal; letter-spacing:0px; line-height:normal; padding:12px 18px 12px 18px; text-align:center; text-decoration:none; border-style:solid;" target="_blank">View report</a>
    </c:if>

    <a href="http://soos.io"><img class="max-width" style="margin-top:20px; display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:100% !important; width:100%; height:auto !important;" width="600" alt="SOOS" data-proportionally-constrained="true" data-responsive="true" src="http://cdn.mcauto-images-production.sendgrid.net/ad9c1fd7de1b8c13/d64e5447-95c9-4fad-a923-4da272364360/848x161.png"></a>
    </div>
</c:if>


