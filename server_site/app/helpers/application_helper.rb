module ApplicationHelper
  
  def add_local_style(local_style)
    @local_styles ||= []
    @local_styles << local_style unless @local_styles.include? local_style
  end
end
