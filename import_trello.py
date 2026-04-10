import os
import requests
import re

API_KEY = os.environ.get("TRELLO_API_KEY")
TOKEN = os.environ.get("TRELLO_TOKEN")
LIST_ID = os.environ.get("TRELLO_LIST_ID")

if not API_KEY or not TOKEN or not LIST_ID:
    raise RuntimeError(
        "Missing TRELLO_API_KEY, TRELLO_TOKEN, or TRELLO_LIST_ID environment variables."
    )

def create_card(name, desc):
    url = "https://api.trello.com/1/cards"
    query = {
        'key': API_KEY,
        'token': TOKEN,
        'idList': LIST_ID,
        'name': name,
        'desc': desc
    }
    response = requests.post(url, params=query)
    return response.json()

def create_checklist(card_id, name="Tasks"):
    url = f"https://api.trello.com/1/cards/{card_id}/checklists"
    query = {
        'key': API_KEY,
        'token': TOKEN,
        'name': name
    }
    response = requests.post(url, params=query)
    return response.json()

def add_check_item(checklist_id, name):
    url = f"https://api.trello.com/1/checklists/{checklist_id}/checkItems"
    query = {
        'key': API_KEY,
        'token': TOKEN,
        'name': name
    }
    response = requests.post(url, params=query)
    return response.json()

def main():
    with open("report.md", "r", encoding="utf-8") as f:
        content = f.read()

    # Get existing cards in the list to skip them
    existing_cards_resp = requests.get(f"https://api.trello.com/1/lists/{LIST_ID}/cards", params={'key': API_KEY, 'token': TOKEN})
    existing_card_names = [c['name'] for c in existing_cards_resp.json()]

    sections = content.split("---")
    for section in sections:
        section = section.strip()
        if not section or "PRODUCT BACKLOG" in section:
            continue
        
        # Extract title
        title_match = re.search(r"## (.*)", section)
        if not title_match:
            continue
        title = title_match.group(1).strip()
        
        if title in existing_card_names:
            # print(f"Skipping: {title}")
            continue

        # Extract User Story
        story_match = re.search(r"User Story:\s*(.*?)(?=\n\n|Checklist:|$)", section, re.DOTALL)
        user_story = story_match.group(1).strip() if story_match else ""
        
        # Extract Checklist items
        checklist_items = re.findall(r"- \[ \] (.*)", section)
        
        # print(f"Creating card: {title}")
        try:
            card = create_card(title, user_story)
            card_id = card.get("id")
            
            if card_id and checklist_items:
                checklist = create_checklist(card_id)
                checklist_id = checklist.get("id")
                if checklist_id:
                    for item in checklist_items:
                        add_check_item(checklist_id, item)
        except Exception as e:
            pass

if __name__ == "__main__":
    main()
