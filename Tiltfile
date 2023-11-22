docker_build(
  ref = 'tilt-sigma-sports-scraper',
  context = './sigma_sports_scraper',
  dockerfile = './sigma_sports_scraper/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'service/build/libs/service-0.0.1-SNAPSHOT.jar'
  ],
)

docker_build(
  ref = 'tilt-product-store',
  context = './product-store',
  dockerfile = './product-store/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'build/libs/product-store-0.0.1-SNAPSHOT.jar'
  ],
)

docker_build(
  ref = 'tilt-user-watching-service',
  context = './user-watching-service',
  dockerfile = './user-watching-service/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'service/build/libs/service-0.0.1-SNAPSHOT.jar'
  ],
)

docker_compose([
  "./docker-compose.yaml",
  encode_yaml({
    "services": {
      "sigma-sports-scraper": {
        "build": {},
        "image": "tilt-sigma-sports-scraper"
      },
      "product-store": {
        "build": {},
        "image": "tilt-product-store"
      },
      "user-watching-service": {
        "build": {},
        "image": "tilt-user-watching-service"
      }
    }
  })
])

