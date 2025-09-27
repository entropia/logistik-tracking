import requests, random

headers = {
    'Accept': 'application/graphql-response+json',
    'Content-Type': 'application/json',
}

ocs = [
	"Finanzen",
    "Backoffice",
    "Content",
    "Heralding",
    "DesignUndMotto",
    "PresseUndSocialMedia",
    "LoungeControl",
    "LoungeTechnik",
    "Infodesk",
    "Merchdesk",
    "Schilder",
    "Badges",
    "Trolle",
    "Kueche",
    "WOC",
    "Fruehstueck",
    "RaumDer1000Namen",
    "Bar",
    "Spaeti",
    "Aussenbar",
    "Kaffeebar",
    "Cocktailbar",
    "NOC",
    "POC",
    "VOC",
    "BuildupAndTeardown",
    "Infrastruktur",
    "Deko",
    "SafeR",
    "SilentHacking",
    "Projektleitung",
    "LOC"
    ]

for i in range(1000):
	json_data = {
        'query': 'mutation CreateList($name: String!) {createPackingList(name: $name) {packingListId}}',
        'variables': {
            'name': f'Test #{i}',
        },
    }
	response = requests.post('http://127.0.0.1:8080/graphql', headers=headers, json=json_data)
	print(response)

# Note: json_data will not be serialized by requests
# exactly as it was in the original request.
#data = '{"query":"\\n    mutation CreateCrate($name: String!, $deli: DeliveryState!, $info: String!, $oc: OperationCenter!) {\\n  createEuroCrate(name: $name, deliveryState: $deli, info: $info, oc: $oc) {\\n    internalId\\n  }\\n}\\n    ","variables":{"info":"Test","oc":"POC","deli":"Transport","name":"Test"}}'
#response = requests.post('http://127.0.0.1:8080/graphql', headers=headers, data=data)