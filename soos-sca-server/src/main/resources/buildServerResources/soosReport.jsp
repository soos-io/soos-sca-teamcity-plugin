<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty url && not url.contains('status')}">
    <div style="margin: auto;
                max-width: 50%;
                text-align: center;">

        <a href="http://soos.io"><img class="max-width" style="display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:100% !important; width:100%; height:auto !important;" width="600" alt="SOOS" data-proportionally-constrained="true" data-responsive="true" src="http://cdn.mcauto-images-production.sendgrid.net/ad9c1fd7de1b8c13/6da70ead-21f6-4c6e-b6fa-572fb3fa647e/1560x415.png"></a>
        <!-- Blue line -->
        <div style="margin:20px 0; padding:0 0 6px 0; background-color: #001369"></div>

        <a href="${url}" style="background-color:#FF486A; border:1px solid #FF486A; border-color:#FF486A; border-radius:6px; border-width:1px; color:#ffffff; display:inline-block; font-size:14px; font-weight:normal; letter-spacing:0px; line-height:normal; padding:12px 18px 12px 18px; text-align:center; text-decoration:none; border-style:solid;" target="_blank">View report</a>

        <!-- Blue line -->
        <div style="margin:20px 0; padding:0 0 6px 0; background-color: #001369"></div>
    </div>
</c:if>


