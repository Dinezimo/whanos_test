def createWhanosJob(String repoName, String gitUrl, String language) {
    job("Projects/project-${repoName}") {
        description("Auto-generated Whanos job for ${repoName} - Language: ${language}")
        
        // Surveiller Git toutes les minutes
        triggers {
            scm('* * * * *')
        }
        
        // Configuration Git
        scm {
            git {
                remote {
                    url(gitUrl)
                }
                branches('main')
                extensions {
                    cloneOptions {
                        shallow(true)
                        timeout(10)
                    }
                }
            }
        }
        
        // Steps de build
        steps {
            shell("""#!/bin/bash
echo "🔨 Whanos Auto-Build: ${repoName}"

# Détection du langage (redondant mais sécurisé)
if [ -f "pom.xml" ]; then
    echo "☕ Java project detected"
    LANGUAGE="java"
elif [ -f "package.json" ]; then
    echo "📦 JavaScript project detected"  
    LANGUAGE="javascript"
else
    echo "❌ Unsupported project type"
    exit 1
fi

# Construction de l'image
echo "🐳 Building Docker image..."
docker build \\
    -t ${repoName}:latest \\
    -f "/var/jenkins_home/workspace/Whanos-Framework/images/\\$LANGUAGE/Dockerfile.standalone" .

echo "✅ Build successful!"

# Vérifier la présence de whanos.yml pour le déploiement
if [ -f "whanos.yml" ]; then
    echo "🚀 whanos.yml found - ready for Kubernetes deployment"
    # Logique Kubernetes à ajouter ici
fi
""")
        }
        
        // Publishers (optionnel - pour les notifications)
        publishers {
            mailer('dev@whanos.com', false, true)
        }
    }
}

// Appel de la fonction avec les paramètres
createWhanosJob("${REPO_NAME}", "${GIT_URL}", "${LANGUAGE}")