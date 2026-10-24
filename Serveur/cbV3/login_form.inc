<?php
/* login_form.inc */
if (!defined('IN_CB')) {
    define('IN_CB', true);
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>CookBook Login</title>
    <link rel="stylesheet" href="sitestyle.css">
</head>
<body>
    <div class="login-container">
        <h2>Connexion CookBook</h2>
        <?php if (isset($message) && $message !== '') : ?>
            <p class="error"><?php echo htmlspecialchars($message, ENT_QUOTES, 'UTF-8'); ?></p>
        <?php endif; ?>
        <form action="site_login.php" method="post">
            <label for="family">Famille :</label>
            <input type="text" id="family" name="family" required><br><br>
            <label for="member">Membre :</label>
            <input type="text" id="member" name="member" required><br><br>
            <label for="password">Mot de passe :</label>
            <input type="password" id="password" name="password" required><br><br>
            <input type="submit" value="Se connecter">
        </form>
    </div>
</body>
</html>
