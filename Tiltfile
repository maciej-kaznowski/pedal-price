docker_build(
  ref = 'tilt-sigma-sports-scraper',
  context = './sigma-sports-scraper-service',
  dockerfile = './sigma-sports-scraper-service/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'build/libs/sigma-sports-scraper-service-0.0.1-SNAPSHOT.jar'
  ],
)

docker_build(
  ref = 'tilt-product-store',
  context = './product-store-service',
  dockerfile = './product-store-service/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'build/libs/product-store-service-0.0.1-SNAPSHOT.jar'
  ],
)

docker_build(
  ref = 'tilt-user-watching-service',
  context = './user-watching-service',
  dockerfile = './user-watching-service/Dockerfile.dev',
  only = [
    'Dockerfile.dev',
    'build/libs/user-watching-service-0.0.1-SNAPSHOT.jar'
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

