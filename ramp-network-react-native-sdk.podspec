require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ramp-network-react-native-sdk"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/RampNetwork/ramp-sdk-rn", :tag => "#{s.version}" }
  # s.source = { :path => '.' }

  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "Ramp"
  s.dependency "React-Core"
end