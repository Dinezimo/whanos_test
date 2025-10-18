echo "🔗 Linking project from $GIT_URL"

REPO_NAME=$(basename -s .git $GIT_URL)

# Clone le repo UTILISATEUR seulement
[ -d "$REPO_NAME" ] && rm -rf "$REPO_NAME"
git clone $GIT_URL

# Détection langage
if [ -f "$REPO_NAME/package.json" ]; then
    LANGUAGE="javascript"
elif [ -f "$REPO_NAME/pom.xml" ]; then
    LANGUAGE="java"
else
    echo "❌ Language not supported"
    exit 1
fi

echo "🧠 Detected language: $LANGUAGE"

# ✅ BUILD MANUEL dans link-project
docker build \
  -t ${REPO_NAME}:latest \
  -f "images/${LANGUAGE}/Dockerfile.standalone" \
  "$WORKSPACE/$REPO_NAME"

echo "✅ Manual build completed"

# ✅ CRÉATION DU JOB AUTO-GÉNÉRÉ avec le DSL
echo "🛠️ Creating automated job with Job DSL..."

# Préparer les paramètres pour le DSL
export REPO_NAME="$REPO_NAME"
export GIT_URL="$GIT_URL"

# Exécuter le Job DSL
echo "📝 Generating job..."
java -jar /var/jenkins_home/war/WEB-INF/jenkins-cli.jar -s http://localhost:8080 groovy = < "whanos-dsl.groovy"

echo "🎉 Automated job 'project-${REPO_NAME}' created in Projects folder!"