db.createUser({
  user: 'wenetProfileManager',
  pwd: 'password',
  roles: [
    {
      role: 'readWrite',
      db: 'wenetProfileManagerDB'
    }
  ]
})
db.profiles.insertOne(
		{
			"id": "1",
			"name": {
				"prefix": null,
				"first": "User",
				"middle": null,
				"last": "1",
				"suffix": null
			},
			"dateOfBirth": {
				"year": 1976,
				"month": 4,
				"day": 1
			},
			"gender": "F",
			"email": "user1@internetofus.eu",
			"phoneNumber": "+34987654321",
			"locale": "es_ES",
			"avatar": "avatar_1",
			"nationality": "Spanish",
			"occupation": null,
			"personalBehaviors": [],
			"_creationTs": 0,
			"_lastUpdateTs": 1234567992,
			"languages": [],
			"norms": [],
			"plannedActivities": [],
			"relevantLocations": [],
			"relationships": [],
			"socialPractices": []
		}
)