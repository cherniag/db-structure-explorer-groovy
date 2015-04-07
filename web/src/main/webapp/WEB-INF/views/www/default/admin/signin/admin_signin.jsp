<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html SYSTEM>
<head lang="en">
    <meta charset="UTF-8"/>
    <title>SignIn</title>
</head>
<body>
<div class="container">
    <section>
        <div class="row show-grid">
            <div class="span6">
                <h3>Sign in</h3>

                <c:if test="${param.error!=null}">
                    <div class="alert alert-error">
                        Please check your login and password
                    </div>
                </c:if>
                <form class="well form-inline" action="signin" method="post">
                    <input name="email" id="email" type="text" placeholder="login"/>
                    <input name="token" id="token" type="password" placeholder="pssword"/>
                    <button type="submit" class="btn btn-primary" >Login</button>
                </form>
            </div>
        </div>
    </section>
</div>
</body>
</html>