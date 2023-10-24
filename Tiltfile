docker_build(
  ref = 'tilt-sigma-sports-scraper',
  context = './sigma_sports_scraper',
  dockerfile = './sigma_sports_scraper/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'build/libs/sigma_sports_scraper-0.0.1-SNAPSHOT.jar'
  ],
)

docker_compose([
  "./docker-compose.yaml",
  encode_yaml({
    "services": {
      "sigma-sports-scraper": {
        "build": {},
        "image": "tilt-sigma-sports-scraper"
      }
    }
  })
])

