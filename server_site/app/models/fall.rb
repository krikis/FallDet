class Fall < ActiveRecord::Base
  
  serialize :details
  
  def details
     self[:details] ||= {}
  end
  
end
