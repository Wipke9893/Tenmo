# TEnmo Database

## How to set up the database

Create a new Postgres database called `tenmo`. Run the `tenmo.sql` script in pgAdmin to set up the database that you'll begin to work from. You'll make structure changes in this script and not the database directly. Additionally, both you and your team members need to run this script each time after making changes to it. 

## Database schema

The provided `tenmo.sql` script creates a single `tenmo_user` table. This table supports `tenmo`'s security system and should only be modified for functionality that supports that purpose.

### `tenmo_user` table

Stores the login information for users of the system.

| Field           | Description                                                                    |
| --------------- | ------------------------------------------------------------------------------ |
| `user_id`       | Unique identifier of the user                                                  |
| `username`      | Unique string the user logs in with |
| `password_hash` | Hashed version of the user's password                                          |

### Testing ###
Any changes to database objects may need to be replicated in the Server's `test-data.sql` if those objects will be
targeted by any of the DAO tests.