<%-- 
    Document   : accueil.jsp
    Author     : Mobile Money Team
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mobile Money</title>
    <style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Cormorant Garamond', 'Playfair Display', Georgia, 'Times New Roman', serif;
}

body {
    background: #faf5ff;
    color: #4a2e5c;
    min-height: 100vh;
    overflow-x: hidden;
    position: relative;
}

/* Dégradé d'arrière-plan */
body::after {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f9f2ff, #f3eaff);
    z-index: -2;
}

/* Motif Dots en arrière-plan */
body::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: radial-gradient(rgba(170, 126, 208, 0.08) 1px, transparent 1px);
    background-size: 30px 30px;
    pointer-events: none;
    z-index: -1;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    position: relative;
    z-index: 1;
}

/* === HEADER === */
header {
    text-align: center;
    padding: 2rem 0;
    position: relative;
}

.logo {
    font-size: 3rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    background: linear-gradient(135deg, #9b6fcf, #c692f0);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    text-shadow: 0 2px 10px rgba(155, 111, 207, 0.15);
    letter-spacing: 2px;
    font-style: italic;
}

.tagline {
    font-size: 0.9rem;
    opacity: 0.7;
    max-width: 600px;
    margin: 0 auto;
    line-height: 1.6;
    font-weight: 400;
    color: #765b91;
    font-style: italic;
}

/* === RECETTE SECTION (sans fond) === */
.recette-section {
    background: transparent;
    border-radius: 24px;
    padding: 1.5rem;
    margin: 2rem 0;
    color: #4a2e5c;
}

.recette-title {
    text-align: center;
    font-size: 1rem;
    font-weight: 400;
    margin-bottom: 1.2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    color: #b17fdb;
    letter-spacing: 6px;
    text-transform: uppercase;
}

.recette-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
    text-align: center;
}

.recette-item {
    background: #ffffff;
    border-radius: 16px;
    padding: 1rem;
    transition: all 0.3s ease;
    border-bottom: 1px solid rgba(170, 126, 208, 0.2);
}

.recette-item:hover {
    background: rgba(233, 217, 255, 0.2);
    transform: scale(1.02);
}

.recette-label {
    font-size: 0.7rem;
    opacity: 0.6;
    margin-bottom: 0.3rem;
    color: #765b91;
    letter-spacing: 3px;
    text-transform: uppercase;
}

.recette-value {
    font-size: 1.8rem;
    font-weight: 600;
    color: #5e3a7c;
}

.recette-value small {
    font-size: 0.7rem;
    font-weight: normal;
    color: #b17fdb;
}

/* === CARDS MENU (Fond blanc pur) === */
.menu-section {
    margin: 2rem 0;
}

.links-section {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
}

.link-card {
    background: #ffffff;
    border-radius: 20px;
    padding: 1.8rem 1.5rem;
    transition: all 0.4s ease;
    border: none;
    border-bottom: 1px solid rgba(170, 126, 208, 0.15);
    position: relative;
    overflow: hidden;
    cursor: pointer;
    text-align: center;
    text-decoration: none;
    color: inherit;
    display: block;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
}

.link-card:hover {
    transform: translateY(-4px);
    border-bottom-color: #b281df;
    background: #ffffff;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.05);
}

.card-icon {
    font-size: 2.2rem;
    margin-bottom: 1rem;
    display: block;
    opacity: 0.8;
}

.card-title {
    font-size: 1.2rem;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: #5e3a7c;
    letter-spacing: 2px;
    font-style: italic;
}

.card-description {
    opacity: 0.6;
    line-height: 1.4;
    font-size: 0.75rem;
    color: #765b91;
    letter-spacing: 1px;
}

/* Badge PDF */
.pdf-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: rgba(233, 217, 255, 0.6);
    color: #b17fdb;
    border-radius: 12px;
    padding: 0.1rem 0.5rem;
    font-size: 0.6rem;
    font-weight: 500;
    letter-spacing: 2px;
    border: 0.5px solid rgba(170, 126, 208, 0.3);
}

.link-card:hover .pdf-badge {
    background: #eeddff;
}

/* === FOOTER === */
footer {
    text-align: center;
    padding: 2rem 0;
    margin-top: 2rem;
    border-top: 1px solid rgba(170, 126, 208, 0.15);
    background: transparent;
    border-radius: 20px;
}

.user-info {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    margin-bottom: 1rem;
}

.user-avatar {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    background: rgba(233, 217, 255, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.3rem;
    color: #b17fdb;
    border: 0.5px solid rgba(170, 126, 208, 0.2);
}

.user-name {
    color: #5e3a7c;
    font-weight: 500;
    letter-spacing: 1px;
}

.copyright {
    opacity: 0.5;
    font-size: 0.8rem;
    color: #765b91;
    letter-spacing: 2px;
}

/* === ANIMATIONS (Lentes et fluides) === */
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(15px); }
    to { opacity: 1; transform: translateY(0); }
}

.fade-in {
    opacity: 0;
    animation: fadeIn 0.9s ease forwards;
}

.delay-1 { animation-delay: 0.15s; }
.delay-2 { animation-delay: 0.3s; }
.delay-3 { animation-delay: 0.45s; }
.delay-4 { animation-delay: 0.6s; }
.delay-5 { animation-delay: 0.75s; }
.delay-6 { animation-delay: 0.9s; }

/* === RESPONSIVE === */
@media (max-width: 768px) {
    .container { padding: 1rem; }
    .logo { font-size: 2.2rem; }
    .links-section { grid-template-columns: repeat(2, 1fr); }
    .recette-grid { grid-template-columns: 1fr; }
}

@media (max-width: 480px) {
    .links-section { grid-template-columns: 1fr; }
    .recette-value { font-size: 1.5rem; }
}
    </style>
</head>
<body>
    <div class="container">
        <!-- HEADER -->
        <header>
            <div class="logo fade-in">💰 Mobile Money</div>
            <p class="tagline fade-in delay-1">Gestion complète des transactions</p>
        </header>

        <!-- RECETTE TOTALE -->
        <div class="recette-section fade-in delay-2">
            <div class="recette-title">
                <span>⚜️</span> RECETTE DE L'OPÉRATEUR <span>⚜️</span>
            </div>
            <div class="recette-grid">
                <div class="recette-item">
                    <div class="recette-label">Frais d'envoi</div>
                    <div class="recette-value" id="fraisEnvoiValue">0 <small>Ar</small></div>
                </div>
                <div class="recette-item">
                    <div class="recette-label">Frais de retrait</div>
                    <div class="recette-value" id="fraisRetraitValue">0 <small>Ar</small></div>
                </div>
                <div class="recette-item">
                    <div class="recette-label">RECETTE TOTALE</div>
                    <div class="recette-value" id="recetteTotaleValue">0 <small>Ar</small></div>
                </div>
            </div>
        </div>


        <!-- MENU PRINCIPAL (sans titre) -->
        <div class="menu-section">
            <div class="links-section">
                <a href="ClientServlet" class="link-card fade-in delay-3">
                    <div class="pdf-badge">📄</div>
                    <div class="card-icon">👥</div>
                    <h3 class="card-title">Clients</h3>
                    <p class="card-description">Gestion des clients<br>relevé PDF</p>
                </a>

                <a href="FraisEnvoiServlet" class="link-card fade-in delay-4">
                    <div class="card-icon">💸</div>
                    <h3 class="card-title">Frais d'envoi</h3>
                    <p class="card-description">Configuration des barèmes</p>
                </a>

                <a href="FraisRecepServlet" class="link-card fade-in delay-5">
                    <div class="card-icon">💳</div>
                    <h3 class="card-title">Frais de retrait</h3>
                    <p class="card-description">Configuration des barèmes</p>
                </a>

                <a href="EnvoiServlet" class="link-card fade-in delay-3">
                    <div class="card-icon">📤</div>
                    <h3 class="card-title">Envoi d'argent</h3>
                    <p class="card-description">Transfert entre clients</p>
                </a>

                <a href="RetraitServlet" class="link-card fade-in delay-4">
                    <div class="card-icon">📥</div>
                    <h3 class="card-title">Retrait d'argent</h3>
                    <p class="card-description">Retrait depuis un compte</p>
                </a>
            </div>
        </div>

        <!-- FOOTER -->
        <footer class="fade-in delay-6">
            <div class="user-info">
                <div class="user-avatar">💰</div>
                <div class="user-name">Mobile Money</div>
            </div>
            <p class="copyright">© 2026 Mobile Money. Tous droits réservés.</p>
        </footer>
    </div>

    <script>
        // Animation au scroll
        document.addEventListener('DOMContentLoaded', () => {
            const fadeElements = document.querySelectorAll('.fade-in');
            const fadeInOnScroll = () => {
                fadeElements.forEach(element => {
                    const elementTop = element.getBoundingClientRect().top;
                    if (elementTop < window.innerHeight - 100) {
                        element.style.opacity = 1;
                        element.style.transform = 'translateY(0)';
                    }
                });
            };
            
            // Initialisation
            fadeElements.forEach(element => {
                element.style.opacity = 0;
                element.style.transform = 'translateY(20px)';
            });
            
            fadeInOnScroll();
            window.addEventListener('scroll', fadeInOnScroll);
        });
    </script>
    <script>
        // Charger la recette via AJAX au chargement de la page
        fetch('RecetteServlet?action=getRecette')
            .then(response => response.json())
            .then(data => {
                document.getElementById('fraisEnvoiValue').innerHTML = formatNumber(data.totalFraisEnvoi) + ' <small>Ar</small>';
                document.getElementById('fraisRetraitValue').innerHTML = formatNumber(data.totalFraisRetrait) + ' <small>Ar</small>';
                document.getElementById('recetteTotaleValue').innerHTML = formatNumber(data.recetteTotale) + ' <small>Ar</small>';
            })
            .catch(error => console.error('Erreur:', error));

        function formatNumber(number) {
            return new Intl.NumberFormat('fr-FR').format(number);
        }
    </script>
</body>
</html>