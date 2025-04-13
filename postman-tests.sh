#!/bin/zsh

COLLECTION="./postman/petclinic-nonregressiontests.postman_collection.json"
ENVIRONMENT="./postman/petclinic-env.postman_environment.json"
REPORT_DIR="./postman/reports"

mkdir -p "$REPORT_DIR"

GREEN='\033[0;32m'
RED='\033[0;31m'
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

  npx newman run "$COLLECTION" \
    -e "$ENVIRONMENT" \
    --folder "$SCENARIO" \
    --reporters cli,html \
    --reporter-html-export "$REPORT_FILE" \
    --timeout-request 5000

  if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ Scenario passed successfully!${NC}"
    echo -e "${GREEN}📄 HTML report generated at: $REPORT_FILE${NC}"
  else
    echo ""
    echo -e "${RED}❌ Scenario failed.${NC}"
    echo -e "${RED}📄 Check the detailed report at: $REPORT_FILE${NC}"
  fi

  echo ""
done


