#!/bin/bash

NODE_ENV=production npm start 2>&1 | tee -a logs/deichkind.log