#!/bin/zsh

COLLECTION="./src/test/postman/petclinic-nonregressiontests.postman_collection.json"
ENVIRONMENT="./src/test/postman/petclinic-env.postman_environment.json"
REPORT_DIR="./src/test/postman/reports"

mkdir -p "$REPORT_DIR"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

# If you create a new scenario, each one should be put on this
# variable on a double quotes to work the script...
SCENARIOS=("01 - First Visit")

echo ""
echo -e "${GREEN}🐾 Starting Petclinic API tests with Newman...${NC}"
sleep 1
echo ""

for SCENARIO in "${SCENARIOS[@]}"; do
  echo "🔎 Scenario: $SCENARIO"
  sleep 1.3

  SAFE_SCENARIO=$(echo "$SCENARIO" | tr ' ' '_' | tr -d '/')
  REPORT_FILE="$REPORT_DIR/${SAFE_SCENARIO}.html"

  if [ ! -w "$REPORT_DIR" ]; then
    echo -e "${RED}❌ Error: No write permission to directory '$REPORT_DIR'.${NC}"
    exit 1
  fi

  npx -p newman -p newman-reporter-htmlextra newman run "$COLLECTION" \
    -e "$ENVIRONMENT" \
    --folder "$SCENARIO" \
    --reporters cli,htmlextra \
    --reporter-htmlextra-export "$REPORT_FILE" \
    --timeout-request 5000 \
    --suppress-exit-code 1

  echo ""
  echo -e "${YELLOW}📄 HTML report generated at: $REPORT_FILE${NC}"
  echo ""

done


